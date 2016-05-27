package com.github.ltsopensource.jobtracker.support.checker;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobNodeType;
import com.github.ltsopensource.core.factory.NamedThreadFactory;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.jobtracker.monitor.JobTrackerMStatReporter;
import com.github.ltsopensource.jobtracker.support.VirtualJobResolver;
import com.github.ltsopensource.queue.WaitingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Check the waitingJobQueue continuously to move the jobs that meet the conditions (such as parent
 * dependencies, data dependencies, etc.) from waitingJobQueue to executableQueue.
 */
public class WaitingJobQueueChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitingJobQueueChecker.class);

    private final ScheduledExecutorService FIXED_EXECUTOR_SERVICE = Executors
            .newScheduledThreadPool(1, new NamedThreadFactory("LTS-WaitingJobQueue-Fix-Executor",
                    true));

    private JobTrackerAppContext appContext;
    private JobTrackerMStatReporter stat;

    public WaitingJobQueueChecker(JobTrackerAppContext appContext) {
        this.appContext = appContext;
        this.stat = (JobTrackerMStatReporter) appContext.getMStatReporter();
    }

    private AtomicBoolean start = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;

    public void start() {
        try {
            if (start.compareAndSet(false, true)) {
                int fixCheckPeriodSeconds = getFixCheckPeriodSeconds();

                scheduledFuture = FIXED_EXECUTOR_SERVICE.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 判断注册中心是否可用，如果不可用，那么直接返回，不进行处理
                            if (!appContext.getRegistryStatMonitor().isAvailable()) {
                                return;
                            }
                            checkAndMove();
                        } catch (Throwable t) {
                            LOGGER.error("Check waiting job queue error ", t);
                        }
                    }
                }, 1, fixCheckPeriodSeconds, TimeUnit.SECONDS);
            }
            LOGGER.info("Waiting job queue checker started!");
        } catch (Throwable e) {
            LOGGER.error("Waiting job queue checker start failed!", e);
        }
    }

    private void checkAndMove() {
        // Get all the jobs in the waiting job queue
        List<JobPo> allJobPoList = getWaitingJobQueue().getAllJobs();
        LOGGER.info("waiting job queue size: " + allJobPoList.size());
        // check if the job can be moved into executable job queue, if so, do it, or do nothing.
        for (JobPo jobPo : allJobPoList) {
            if(meetDependencies(jobPo)) {
                // TODO(zj): maybe should consider concurrency issue about two queues.)
                getWaitingJobQueue().remove(jobPo.getWorkflowId(), jobPo.getSubmitTime(),
                        jobPo.getJobName(), jobPo.getTriggerTime());
                JobNodeType jobNodeType = jobPo.getJobNodeType();
                switch (jobNodeType) {
                    case START_JOB:
                        appContext.getExecutableJobQueue().add(jobPo);
                        break;
                    case END_JOB:
                    case FORK_JOB:
                    case JOIN_JOB:
                    case DECISION_JOB:
                        VirtualJobResolver.handleVirtualJobWhenFinish(jobPo, appContext);
                        break;
                    case SHELL_JOB:
                    case URL_JOB:
                        appContext.getExecutableJobQueue().add(jobPo);
                        break;
                }
            }
        }

    }

    private WaitingJobQueue getWaitingJobQueue() {
        return appContext.getWaitingJobQueue();
    }

    private boolean meetDependencies(JobPo jobPo) {
//        if (true) {
//            return false;
//        }
        boolean result = false;
        switch (jobPo.getJobType()) {
            case TRIGGER_TIME:
            case REAL_TIME:
                result = checkParents(jobPo, JobUtils.getParentList(jobPo));
                break;
            case CRON:
                if (jobPo.getJobNodeType().equals(JobNodeType.START_JOB)) {
                    if (JobUtils.isRelyOnPrevCycle(jobPo)) {
                        result = checkPreviousWorkflow(jobPo);
                    } else {
                        result = true;
                    }
                } else {
                    result = checkParents(jobPo, JobUtils.getParentList(jobPo));
                }
                break;
            case REPEAT:
                result = checkDependencies4RepeatJob(jobPo);
                break;
        }
        return result;
    }

    private boolean checkDependencies4RepeatJob(JobPo jobPo) {
        // TODO(zj): To be implemented
        return false;
    }

    /**
     * Check dependencies for single period job, which includes realTime job and triggerTime job.
     *
     * @param jobPo jobPo info for this job
     * @return true if the jobPo meets the dependencies
     */
    private boolean checkDependencies4SinglePeriodJob(JobPo jobPo) {
        // TODO (zj: this job should wait until it's parents finish the execution.)
        String parents = jobPo.getExtParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY);
        LOGGER.info("......enter meetDependencies");
        LOGGER.info("job name: " + jobPo.getJobName() + ", parents: " + parents);
        if (StringUtils.isEmpty(parents)) {
            LOGGER.info("......no parents, exit meetDependencies, true");
            return true;
        }

        String workflowId = jobPo.getWorkflowId();
        LOGGER.info("job workflowId: " + workflowId);
        LOGGER.info("submit_time: " + jobPo.getSubmitTime());
        LOGGER.info("trigger_time: " + jobPo.getTriggerTime());


        String[] parentNames = StringUtils.splitWithTrim(parents, JobInfoConstants
                        .JOB_PARENTS_CHILDREN_SEPARATOR);
        LOGGER.info("parents size:" + parentNames.length);
        LOGGER.info("parents:" + parents);

        for (String parentName : parentNames) {
            // TODO(zj): workflow_id, submit_time, job_name, trigger_time
            LOGGER.info("...........................parentName:" + parentName);
            JobLogPo parentLog = appContext.getJobLogger().getJobLogPo(workflowId, jobPo
                    .getSubmitTime(), parentName, jobPo.getTriggerTime());
            LOGGER.info("parentLog:" + parentLog);

            if (parentLog == null) {
                LOGGER.info("......exit meetDependencies, false");
                return false;
            }
        }
        LOGGER.info("......exit meetDependencies, true");
        return true;
    }

    /**
     * Check if the previous period of workflow has already finished.
     *
     * @param jobPo indicates job info
     * @return true if the previous period has finished or this period itself is first one
     */
    private boolean checkPreviousWorkflow(JobPo jobPo) {
        final Long previousTriggerTime = getPreviousTriggerTime(jobPo);
        if (previousTriggerTime == null) {
            // This is the first period of workflow, no previous workflow.
            return true;
        } else {
            // Check if the end job of previous workflow has already finished.
            JobLogPo parentLog = appContext.getJobLogger().getJobLogPo(jobPo.getWorkflowId(),
                    jobPo.getSubmitTime(), JobInfoConstants.END_JOB_NAME, previousTriggerTime);
            return parentLog != null;
        }
    }

    private Long getPreviousTriggerTime(JobPo jobPo) {

        String triggerTime = jobPo.getInternalExtParam(JobInfoConstants.CRON_JOB_LAST_TRIGGER_TIME_KEY);
        if (StringUtils.isEmpty(triggerTime)){
            return null;
        } else {
            return Long.valueOf(triggerTime.trim());
        }
    }

    /**
     * Checks if all the parents have already finished.
     *
     * @param jobPo indicate a job info
     * @param parentList parent job name list
     * @return true if all the parents have already finished or no parents
     */
    private boolean checkParents(JobPo jobPo, List<String> parentList) {
        if (CollectionUtils.isEmpty(parentList)) {
            return true;
        }
        for (String parent : parentList) {
            JobLogPo parentLog = appContext.getJobLogger().getJobLogPo(jobPo.getWorkflowId(),
                    jobPo.getSubmitTime(), parent, jobPo.getTriggerTime());
            if (parentLog == null) {
                return false;
            }
        }
        return true;
    }

    private int getFixCheckPeriodSeconds() {
        // TODO (zj: 10 is default value, should be extracted)
        int fixCheckPeriodSeconds = appContext.getConfig().getParameter(ExtConfig
                .JOB_TRACKER_WAITING_JOB_FIX_CHECK_INTERVAL_SECONDS, 10);

        if (fixCheckPeriodSeconds < 5) {
            fixCheckPeriodSeconds = 5;
        } else if (fixCheckPeriodSeconds > 5 * 60) {
            fixCheckPeriodSeconds = 5 * 60;
        }
        return fixCheckPeriodSeconds;
    }

    public void stop() {
        try {
            if (start.compareAndSet(true, false)) {
                scheduledFuture.cancel(true);
                FIXED_EXECUTOR_SERVICE.shutdown();
            }
            LOGGER.info("Waiting job queue checker stopped!");
        } catch (Throwable t) {
            LOGGER.error("Waiting job queue checker stop failed!", t);
        }
    }

}
