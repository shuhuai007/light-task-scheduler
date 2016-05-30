package com.github.ltsopensource.queue;

import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.store.jdbc.exception.DupEntryException;

import java.util.List;

/**
 * 暂停队列
 *
 * @author Robert HG (254963746@qq.com) on 5/27/15.
 */
public interface SuspendJobQueue extends JobQueue{

    /**
     * 添加任务
     *
     * @throws DupEntryException
     */
    boolean add(JobPo jobPo);

    JobPo getJob(String jobId);

    /**
     * 移除Cron Job
     */
    boolean remove(String jobId);

    /**
     * Gets list of {@link JobPo} based on workflowId.
     *
     * @param workflowId workflow id of the lts task
     * @return list of {@link JobPo}
     */
    List<JobPo> getJobsByWorkflowId(String workflowId);
}
