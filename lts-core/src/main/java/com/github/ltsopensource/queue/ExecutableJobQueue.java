package com.github.ltsopensource.queue;

import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;

/**
 * 等待执行的任务队列 (可以有多个)
 *
 * @author Robert HG (254963746@qq.com) on 5/28/15.
 */
public interface ExecutableJobQueue extends JobQueue {

    /**
     * 创建一个队列
     */
    boolean createQueue(String taskTrackerNodeGroup);

    /**
     * 删除
     */
    boolean removeQueue(String taskTrackerNodeGroup);

    /**
     * 入队列
     */
    boolean add(JobPo jobPo);

    /**
     * 出队列
     */
    boolean remove(String taskTrackerNodeGroup, String jobId);

    boolean removeBatch(String realTaskId, String taskTrackerNodeGroup);

    /**
     * reset , runnable
     */
    void resume(JobPo jobPo);

    /**
     * 得到死任务
     */
    List<JobPo> getDeadJob(String taskTrackerNodeGroup, long deadline);

    /**
     * 得到JobPo
     */
    JobPo getJob(String taskTrackerNodeGroup, String taskId);

    boolean remove(String taskTrackerNodeGroup, String jobId, Long triggerTime);

    /**
     * Gets list of {@link JobPo} based on workflow id.
     *
     * @param workflowId workflow id of the lts task
     * @param taskTrackerGroupName task tracker group name
     * @return list of {@link JobPo}
     */
    List<JobPo> getJobsByWorkflowId(String workflowId, String taskTrackerGroupName);

    /**
     * Remove batch based on workflowId.
     *
     * @param workflowId workflow id of the lts task
     * @param taskTrackerGroupName task tracker group name
     * @return
     */
    boolean removeBatchByWorkflowId(String workflowId, String taskTrackerGroupName);

    /**
     * Gets list of {@link JobPo} based on workflowId, submitTime, jobName.
     *
     * @param workflowId workflow id of the lts task
     * @param submitTime submit time of the lts task
     * @param jobName job name of the lts task
     * @param taskTrackerGroupName task tracker group name
     * @return
     */
    List<JobPo> getJobs(String workflowId, Long submitTime, String jobName, String taskTrackerGroupName);
}
