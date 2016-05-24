package com.github.ltsopensource.jobtracker.support.checker;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.LogType;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.domain.JobType;
import com.github.ltsopensource.core.factory.NamedThreadFactory;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.support.JobDomainConverter;
import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.jobtracker.complete.JobFinishHandler;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.jobtracker.monitor.JobTrackerMStatReporter;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.Date;
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
        // get all the jobs in the waiting job queue
        List<JobPo> allJobPoList = appContext.getWaitingJobQueue().getAllJobs();
        LOGGER.info("waiting job queue size: " + allJobPoList.size());
        // check if the job can be moved into executable job queue, if so, do it, or do nothing.
        for (JobPo jobPo:allJobPoList) {
            if(meetDependencies(jobPo)) {
                // TODO (zj: maybe should consider concurrency issue about two queues.)
                appContext.getWaitingJobQueue().remove(jobPo.getTaskTrackerNodeGroup(), jobPo
                        .getJobId());
                if (isEndNodeJob(jobPo)) {
                    // end node job is a dummy job, no need to be added into executable queue
                    handleEndJob(jobPo);

                } else {
                    appContext.getExecutableJobQueue().add(jobPo);
                }
            }
        }

    }

    /**
     * Handles the end job.
     * @param jobPo indicate a job info
     */
    private void handleEndJob(JobPo jobPo) {
        // 1. generate next end job for next trigger time
        new JobFinishHandler(appContext).finishCronJob(jobPo.getJobId());
        // 2. record this end job info
        JobLogPo jobLogPo = JobDomainConverter.convertJobLog(jobPo);
        jobLogPo.setMsg("End Job Finished");
        jobLogPo.setLogType(LogType.FINISHED);
        jobLogPo.setSuccess(true);
        jobLogPo.setTaskTrackerIdentity(jobPo.getTaskTrackerIdentity());
        jobLogPo.setLevel(Level.INFO);
        jobLogPo.setLogTime(new Date().getTime());
        appContext.getJobLogger().log(jobLogPo);
    }

    /**
     * Checks if the job is end node job.
     * @param jobPo indicate a job info
     * @return true if the job is end node job
     */
    private boolean isEndNodeJob(JobPo jobPo) {
        String isEndString = jobPo.getExtParams().get("isEnd");
        if (StringUtils.isNotEmpty(isEndString) && isEndString.equals("true")) {
            return true;
        }
        return false;
    }

    private boolean meetDependencies(JobPo jobPo) {
        // TODO (zj: keep the cron job in the waiting queue for testing)
        boolean result = false;
        switch (jobPo.getJobType()) {
            case TRIGGER_TIME:
            case REAL_TIME:
                result = checkDependencies4SinglePeriodJob(jobPo);
                break;
            case CRON:
                List<String> parentList = JobUtils.getParentList(jobPo);
                if (parentList == null) {
                    result = checkPreviousWorkflow(jobPo);
                } else {
                    result = checkParents(jobPo, parentList);
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
        LOGGER.debug("......enter meetDependencies");
        LOGGER.debug("job name: " + jobPo.getJobName() + ", parents: " + parents);
        if (StringUtils.isEmpty(parents)) {
            LOGGER.info("......no parents, exit meetDependencies, true");
            return true;
        }

        String workflowId = jobPo.getWorkflowId();
        LOGGER.debug("job workflowId: " + workflowId);

        String[] parentNames = StringUtils.splitWithTrim(parents,
                JobInfoConstants.JOB_PARENTS_CHILDREN_SEPARATOR);
        LOGGER.debug("parents size:" + parentNames.length);

        for (String parentName : parentNames) {
            // TODO(zj): workflow_id, job_name, trigger_time
            JobLogPo parentLog = appContext.getJobLogger().search(workflowId, parentName);
            LOGGER.debug("parentLog:" + parentLog);

            if (parentLog == null) {
                LOGGER.debug("......exit meetDependencies, false");
                return false;
            }
        }
        LOGGER.debug("......exit meetDependencies, true");
        return true;
    }

    private boolean checkPreviousWorkflow(JobPo jobPo) {
        // TODO (zj: should check if the endJob of last workflow finishes)
        final Long previousTriggerTime = getPreviousTriggerTime(jobPo);
        if (previousTriggerTime == null) {
            // this is first workflow instance, no previous workflow
            return true;
        } else {
            // check if the previous workflow's end job has already finished
            JobLogPo parentLog = appContext.getJobLogger().
                    search(jobPo.getExtParams().get("workflowStaticId"),
                            jobPo.getExtParams().get("submitInstanceId"),
                            previousTriggerTime, "job_cron_end");
            return parentLog != null;
        }
    }

    private Long getPreviousTriggerTime(JobPo jobPo) {

        String triggerTime = jobPo.getInternalExtParam("lastTriggerTime");
        if (StringUtils.isEmpty(triggerTime)){
            return null;
        } else {
            return Long.valueOf(triggerTime.trim());
        }
    }

    /**
     * Checks if all the parents have already finished.
     * @param jobPo indicate a job info
     * @param parentList parent id list
     * @return true if all the parents have already finished
     */
    private boolean checkParents(JobPo jobPo, List<String> parentList) {
        for (String parentId : parentList) {
            // parentId(task_id), workflowStaticId, submitInstanceId, trigger_time
            JobLogPo parentLog = appContext.getJobLogger().
                    search(jobPo.getExtParams().get("workflowStaticId"),
                            jobPo.getExtParams().get("submitInstanceId"),
                            jobPo.getTriggerTime(), parentId);
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
