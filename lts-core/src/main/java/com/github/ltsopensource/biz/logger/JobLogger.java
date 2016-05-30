package com.github.ltsopensource.biz.logger;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.JobLoggerRequest;
import com.github.ltsopensource.admin.response.PaginationRsp;

import java.util.List;

/**
 * 执行任务日志记录器
 *
 * @author Robert HG (254963746@qq.com) on 3/24/15.
 */
public interface JobLogger {

    public void log(JobLogPo jobLogPo);

    public void log(List<JobLogPo> jobLogPos);

    public PaginationRsp<JobLogPo> search(JobLoggerRequest request);

    public JobLogPo search(String workflowId, String taskId);

    public JobLogPo search(String workflowStaticId, String submitInstanceId, Long triggerTime,
                          String taskId);

    /**
     * Gets {@link JobLogPo} object according to workflowId, submitTime, jobName, triggerTime.
     *
     * @param workflowId workflow id of the lts task
     * @param submitTime submit time of the lts task
     * @param jobName job name of this job
     * @param triggerTime plan time of this job
     * @return JobLogPo if exists
     */
    JobLogPo getJobLogPo(String workflowId, Long submitTime, String jobName, Long triggerTime);

    /**
     * Gets list of {@link JobLogPo} accroding to workflowId, submitTime, triggerTime, including job that was killed
     * or finished normally.
     *
     * @param workflowId workflow id of the lts task
     * @param submitTime submit time of the lts task
     * @param triggerTime plan time
     * @return list of {@link JobLogPo}
     */
    List<JobLogPo> getJobLogPoListWithEndStatus(String workflowId, Long submitTime, Long triggerTime);

    /**
     * Gets the max submit time according to workflowId and triggerTime.
     * @param workflowId workflow id of the lts task
     * @param triggerTime plan time
     * @return max timestamp of submitTime
     */
    Long getMaxSubmitTime(String workflowId, Long triggerTime);

    /**
     * Remove {@link JobLogPo}.
     *
     * @param jobLogPo job log info
     * @return true if remove successfully
     */
    boolean remove(JobLogPo jobLogPo);
}