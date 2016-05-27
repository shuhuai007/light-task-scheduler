package com.github.ltsopensource.jobtracker.support.scheduler;

import com.github.ltsopensource.core.commons.utils.*;
import com.github.ltsopensource.core.constant.Constants;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.domain.JobType;
import com.github.ltsopensource.core.exception.LtsRuntimeException;
import com.github.ltsopensource.core.factory.NamedThreadFactory;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.support.CronExpressionUtils;
import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.core.support.NodeShutdownHook;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.support.NonRelyJobUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Scheduler for cron job, generating batch jobs based on cron expression.
 */
public class CronJobScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CronJobScheduler.class);
    private JobTrackerAppContext appContext;
    private int scheduleIntervalMinute;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;
    private AtomicBoolean running = new AtomicBoolean(false);

    private AtomicBoolean start = new AtomicBoolean(false);

    public CronJobScheduler(JobTrackerAppContext appContext) {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());
        this.appContext = appContext;
        this.scheduleIntervalMinute = this.appContext.getConfig().getParameter(
                ExtConfig.JOB_TRACKER_CRON_JOB_SCHEDULER_INTERVAL_MINUTE, 10);

        NodeShutdownHook.registerHook(appContext, this.getClass().getSimpleName(), new Callable() {
            @Override
            public void call() throws Exception {
                stop();
            }
        });
    }

    public void start() {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());

        if (!start.compareAndSet(false, true)) {
            return;
        }
        try {
            executorService = Executors.newScheduledThreadPool(1,
                    new NamedThreadFactory(CronJobScheduler.class.getSimpleName(), true));
            this.scheduledFuture = executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (running.compareAndSet(false, true)) {
                            try {
                                schedule();
                            } finally {
                                running.set(false);
                            }
                        }
                    } catch (Throwable t) {
                        LOGGER.error("Error On Schedule", t);
                    }
                }
            }, 10, (scheduleIntervalMinute - 1) * 60, TimeUnit.SECONDS);
        } catch (Throwable t) {
            LOGGER.error("Scheduler Start Error", t);
        }
    }

    public void stop() {
        if (!start.compareAndSet(true, false)) {
            return;
        }
        try {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            if (executorService != null) {
                executorService.shutdownNow();
                executorService = null;
            }
        } catch (Throwable t) {
            LOGGER.error("Scheduler Stop Error", t);
        }
    }

    private void schedule() {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("========= Scheduler start =========");
        }

        Date now = new Date();
        Date checkTime = DateUtils.addMinute(now, 10);
        //  cron任务
        while (true) {
            List<JobPo> cronJobPoList = appContext.getCronJobQueue().getNeedGenerateJobPos(10);
            if (CollectionUtils.sizeOf(cronJobPoList) == 0) {
                break;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("========= CronJob size[{}] =========", CollectionUtils.sizeOf(cronJobPoList));
            }
            for (JobPo cronJobPo : cronJobPoList) {
                generateJob4CronJob(cronJobPo);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("========= Scheduler End =========");
        }
    }

    /**
     * Generate jobs for cron job based on cron expression, and put these jobs into waiting job
     * queue.
     *
     * @param cronJobPo JobPo for cron job
     */
    private void generateJob4CronJob(JobPo cronJobPo) {
        String throttleStr = cronJobPo.getExtParam(JobInfoConstants
                .JOB_PARAM_COORDINATOR_CONTROLS_THROTTLE_KEY);
        LOGGER.info("cronJobPo, job name:" + cronJobPo.getJobName() + ", throttle:" + throttleStr);
        final int throttle = Integer.valueOf(throttleStr);
        final int stillWaiting = appContext.getWaitingJobQueue().getJobs(cronJobPo.getJobId()).size();
        int needGenerateNum = throttle - stillWaiting;
        LOGGER.info("cronJobPo, job name:" + cronJobPo.getJobName() +
                ",needGenerateNum:" + needGenerateNum);

        Long lastGenerateTriggerTime = cronJobPo.getLastGenerateTriggerTime();
        while (needGenerateNum-- > 0) {
            Long triggerTime = generateTriggerTime(lastGenerateTriggerTime, cronJobPo);
            JobPo jobPo = JobUtils.copy(cronJobPo);
            jobPo.setJobId(JobUtils.generateJobId());
            jobPo.setTaskTrackerIdentity(null);
            jobPo.setIsRunning(false);
            jobPo.setTriggerTime(triggerTime);
            jobPo.setGmtModified(SystemClock.now());
            jobPo.setInternalExtParam(Constants.ONCE, Boolean.TRUE.toString());
            LOGGER.info("job name:" + jobPo.getJobName() + "; job id:" + jobPo.getJobId() + ";" +
                    "needGenerateNum:" + needGenerateNum);

            appContext.getWaitingJobQueue().add(jobPo);
            lastGenerateTriggerTime = triggerTime;
        }

        appContext.getCronJobQueue().updateLastGenerateTriggerTime(cronJobPo.getJobId(),
                lastGenerateTriggerTime);

    }

    private Long generateTriggerTime(Long lastGenerateTriggerTime, JobPo cronJobPo) {
        Long timeStamp = lastGenerateTriggerTime;
        if (lastGenerateTriggerTime == null || lastGenerateTriggerTime == 0) {
            timeStamp = cronJobPo.getStartTime();
        }
        String cronExpression = cronJobPo.getCronExpression();

        return CronExpressionUtils.getNextTriggerTime(cronExpression,
                new Date(timeStamp)).getTime();
    }

    /**
     * Add cronJobPo into cronJobScheduler.
     *
     * @param cronJobPo
     */
    public void addScheduleJob(JobPo cronJobPo) {
        Assert.isTrue(cronJobPo.getJobType().equals(JobType.CRON));
        if (!JobUtils.isRelyOnPrevCycle(cronJobPo)) {
            generateJob4CronJob(cronJobPo);
        }
    }
}
