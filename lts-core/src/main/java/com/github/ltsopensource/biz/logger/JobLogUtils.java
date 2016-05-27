package com.github.ltsopensource.biz.logger;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.LogType;
import com.github.ltsopensource.biz.logger.domain.WorkflowLogType;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.support.JobDomainConverter;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 4/6/16.
 */
public class JobLogUtils {

    public static void log(LogType logType, JobPo jobPo, JobLogger jobLogger) {
        JobLogPo jobLogPo = JobDomainConverter.convert2JobLog(jobPo);
        jobLogPo.setSuccess(true);
        jobLogPo.setLogType(logType);
        jobLogPo.setLogTime(SystemClock.now());
        jobLogPo.setLevel(Level.INFO);
        jobLogger.log(jobLogPo);
    }

    /**
     * Write the {@link JobPo} into log table.
     *
     * @param logType log type
     * @param jobPo indicates {@link JobPo} object
     * @param workflowLogType log type of workflow
     * @param jobLogger job logger
     */
    public static void log(LogType logType, JobPo jobPo, WorkflowLogType workflowLogType, JobLogger
            jobLogger) {
        JobLogPo jobLogPo = JobDomainConverter.convert2JobLog(jobPo);
        jobLogPo.setSuccess(true);
        jobLogPo.setLogType(logType);
        jobLogPo.setLogTime(SystemClock.now());
        jobLogPo.setLevel(Level.INFO);
        jobLogPo.setInternalExtParam(
                JobInfoConstants.JOB_LOG_PO_INTERNAL_PARAM_WORKFLOW_LOG_TYPE_KEY,
                workflowLogType.name());
        jobLogger.log(jobLogPo);
    }


    public static void logBatch(LogType logType, List<JobPo> jobPoList, WorkflowLogType workflowLogType, JobLogger jobLogger) {
        for (JobPo jobPo : jobPoList) {
            log(logType, jobPo, workflowLogType, jobLogger);
        }
    }
}
