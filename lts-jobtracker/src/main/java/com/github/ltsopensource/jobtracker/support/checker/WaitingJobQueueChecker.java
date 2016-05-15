package com.github.ltsopensource.jobtracker.support.checker;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.factory.NamedThreadFactory;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.jobtracker.monitor.JobTrackerMStatReporter;
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
        // get all the jobs in the waiting job queue
        List<JobPo> allJobPoList = appContext.getWaitingJobQueue().getAllJobs();
        LOGGER.info("waiting job queue size: " + allJobPoList.size());
        // check if the job can be moved into executable job queue, if so, do it, or do nothing.
        for (JobPo jobPo:allJobPoList) {
            if(meetDependencies(jobPo)) {
                // TODO (zj: maybe should consider concurrency issue about two queues.
                appContext.getWaitingJobQueue().remove(jobPo.getTaskTrackerNodeGroup(), jobPo
                        .getJobId());
                appContext.getExecutableJobQueue().add(jobPo);
            }
        }

    }

    private boolean meetDependencies(JobPo jobPo) {
        // TODO (zj: keep the cron job in the waiting queue for testing)
        if (jobPo.isCron()) {
            List<String> parentList = JobUtils.getParentList(jobPo);
            if (parentList == null) {
                // TODO (zj: no parents, it is first level node)
                return checkLastWorkflow(jobPo);
            } else {
                return checkParents(jobPo, parentList);
            }
        }

        // TODO (zj: this job should wait until it's parents finish the execution.)
        String parents = jobPo.getExtParams().get("parents");
        LOGGER.info("......enter meetDependencies");
        LOGGER.info("job task_id: " + jobPo.getTaskId() + ", parents: " + parents);
        if (StringUtils.isEmpty(parents)) {
            LOGGER.info("......exit meetDependencies, true");
            return true;
        }

        String workflowId = jobPo.getExtParams().get("wfInstanceId");
        LOGGER.info("job workflowId: " + workflowId);

        String[] parentIds = StringUtils.splitWithTrim("\001", parents);
        LOGGER.info("parentIds's size:" + parentIds.length);

        for (String parentId : parentIds) {
            LOGGER.info(" for loop, parentId:" + parentId);
            LOGGER.info(" for loop, workflowId:" + workflowId);

            JobLogPo parentLog = appContext.getJobLogger().search(workflowId, parentId);
            LOGGER.info("parentLog:" + parentLog);

            if (parentLog == null) {
                LOGGER.info("......exit meetDependencies, false");
                LOGGER.info("\n\n");
                return false;
            }
        }
        LOGGER.info("......exit meetDependencies, true");
        LOGGER.info("\n\n");

        return true;
    }

    private boolean checkLastWorkflow(JobPo jobPo) {
        // TODO (zj: should check if the endJob of last workflow finishes
        return true;
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
