package com.github.ltsopensource.queue;

import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;

/**
 * 等待进入{@link ExecutableJobQueue}的任务队列
 *
 */
public interface WaitingJobQueue extends JobQueue {

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

    /**
     * Gets all the JobPo.
     */
    List<JobPo> getAllJobs();

    /**
     * Remove the {@link JobPo} object from waiting queue.
     *
     * @param workflowId workflow id of the lts task
     * @param submitTime submit time of the lts task
     * @param jobName job name of this job
     * @param triggerTime plan time of this job
     * @return true if removed successfully
     */
    boolean remove(String workflowId, Long submitTime, String jobName, Long triggerTime);

    /**
     * Gets jobs based on jobId.
     *
     * @param jobId generated when submitting
     * @return list of JobPo object
     */
    List<JobPo> getJobs(String jobId);
}
