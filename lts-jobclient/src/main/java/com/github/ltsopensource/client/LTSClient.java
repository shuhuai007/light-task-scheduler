package com.github.ltsopensource.client;

import com.github.ltsopensource.client.operation.SubmitOperation;
import com.github.ltsopensource.client.domain.LTSTask;

/**
 * Client API to submit and manage lts task.
 */
public class LTSClient {

    private String jobTrackerUrl;
    private String zookeeperIP;
    private String zookeeperPort;

    public LTSClient() {
    }

    /**
     * Create a lts client instance.
     *
     * @param jobTrackerUrl URL of the jobTracker instance it will interact with.
     */
    public LTSClient(String jobTrackerUrl) {
        this.jobTrackerUrl = jobTrackerUrl;
    }

    /**
     * Create a lts client instance.
     *
     * @param zookeeperIP ip points to zookeeper
     * @param zookeeperPort port of zookeeper server
     */
    public LTSClient(String zookeeperIP, String zookeeperPort) {
        this.zookeeperIP = zookeeperIP;
        this.zookeeperPort = zookeeperPort;
    }

    /**
     * Submit a task.
     *
     * @param jdl JSON dispatch language to describe the whole task
     * @param taskId taskId generated from task database table
     * @throws LTSClientException the LTS client exception
     */
    public void submit(String jdl, String taskId) throws LTSClientException {
        if (!verifyJDL(jdl)) {
            throw new LTSClientException();
        } else {
            // TODO(zj): to be implemented
            LTSTask ltsTask = generateLTSTask(jdl, taskId);
            new SubmitOperation(ltsTask).call();
        }
    }

    private LTSTask generateLTSTask(String jdl, String taskId) {
        // TODO(zj): to be implemented
        return null;
    }

    private boolean verifyJDL(String jdl) {
        // TODO(zj): to be implemented
        return true;
    }

    /**
     * Kill a task.
     *
     * @param taskId generated from task database table
     * @throws LTSClientException the LTS client exception
     */
    public void kill(String taskId) throws LTSClientException {
        if (!validateTaskId(taskId)) {
            throw new LTSClientException();
        } else {
            // TODO(zj): to be implemented
        }
    }

    private boolean validateTaskId(String taskId) {
        // TODO(zj): to be implemented
        return true;
    }

    /**
     * Suspend a task.
     *
     * @param taskId generated from task database table
     * @throws LTSClientException the LTS client exception
     */
    public void suspend(String taskId) throws LTSClientException {
        if (!validateTaskId(taskId)) {
            throw new LTSClientException();
        } else {
            // TODO(zj): to be implemented
        }
    }

    /**
     * Resume a workflow job.
     *
     * @param taskId generated from task database table
     * @throws LTSClientException the LTS client exception
     */
    public void resume(String taskId) throws LTSClientException {
        if (!validateTaskId(taskId)) {
            throw new LTSClientException();
        } else {
            // TODO(zj): to be implemented
        }
    }

    /**
     * Rerun a task instance based on plan time.
     *
     * @param taskId generated from task database table
     * @param planTime plan time of this task instance
     * @throws LTSClientException
     */
    public void reRun(String taskId, String planTime) throws LTSClientException {
        if (!validateTaskId(taskId)) {
            throw new LTSClientException();
        }
        if (!validatePlanTime(planTime)) {
            throw new LTSClientException();
        }
        // TODO(zj): to be implemented
    }

    private boolean validatePlanTime(String planTime) {
        return true;
    }
}
