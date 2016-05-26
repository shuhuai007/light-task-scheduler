package com.github.ltsopensource.jobtracker.support;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.LogType;
import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.domain.JobType;
import com.github.ltsopensource.core.support.JobDomainConverter;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.jobtracker.complete.JobFinishHandler;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.Date;

/**
 * Support class for resolving operation about virtual job.
 */
public class VirtualJobResolver {

    /**
     * Handle virtual job when finishing.
     *
     * @param jobPo indicates a job info
     * @param appContext application context for jobTracker
     */
    public static void handleVirtualJobWhenFinish(JobPo jobPo, JobTrackerAppContext appContext) {
        // Write log.
        writeFinishLog(jobPo, appContext);
        // Generate next period.
        if (jobPo.getRelyOnPrevCycle().booleanValue() && jobPo.getJobType().equals(JobType.CRON)) {
            new JobFinishHandler(appContext).finishCronJob(jobPo.getJobId());
        }
    }

    /**
     * Write log into lts_job_log_po when job finishes.
     *
     * @param jobPo indicates a job info
     * @param appContext application context for jobTracker
     */
    public static void writeFinishLog(JobPo jobPo, JobTrackerAppContext appContext) {
        JobLogPo jobLogPo = JobDomainConverter.convert2JobLog(jobPo);
        jobLogPo.setMsg("Job Finished");
        jobLogPo.setLogType(LogType.FINISHED);
        jobLogPo.setSuccess(true);
        jobLogPo.setTaskTrackerIdentity(jobPo.getTaskTrackerIdentity());
        jobLogPo.setLevel(Level.INFO);
        jobLogPo.setLogTime(new Date().getTime());
        jobLogPo.setExecutingStart(SystemClock.now());
        jobLogPo.setExecutingEnd(SystemClock.now());
        appContext.getJobLogger().log(jobLogPo);
    }
}
