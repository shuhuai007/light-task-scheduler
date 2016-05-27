package com.github.ltsopensource.client;

import com.github.ltsopensource.client.operation.KillOperation;
import com.github.ltsopensource.client.operation.SubmitOperation;
import com.github.ltsopensource.client.utils.JDLParser;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.domain.LTSTask;

/**
 * Client API to submit and manage lts task.
 */
public class LTSClient {

    private String taskTrackGroupName;
    private String jobTrackerUrl;
    private String zookeeperIP;
    private String zookeeperPort;
    private String jobTrackerGroupName;

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
     * @param jobTrackerGroupName group name of jobTracker
     */
    public LTSClient(String zookeeperIP, String zookeeperPort, String jobTrackerGroupName) {
        this.zookeeperIP = zookeeperIP;
        this.zookeeperPort = zookeeperPort;
        this.jobTrackerGroupName = jobTrackerGroupName;
    }

    /**
     * Submit a task.
     *
     * @param jdl JSON dispatch language to describe the whole task
     * @param taskId taskId generated from task database table
     * @param taskTrackerGroupName node group name
     * @throws LTSClientException the LTS client exception
     */
    public void submit(String jdl, String taskId, String taskTrackerGroupName) throws LTSClientException {
        if(JDLParser.verifyJDL(jdl)){
            LTSTask ltsTask = JDLParser.generateLTSTask(jdl, taskId, taskTrackerGroupName);
            new SubmitOperation(ltsTask, zookeeperIP, zookeeperPort, jobTrackerGroupName).call();
        } else {
            throw new LTSClientException("jdl can pass validation, please check your jdl");
        }
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
            new KillOperation(taskId, zookeeperIP, zookeeperPort, jobTrackerGroupName).call();
        }
    }

    private boolean validateTaskId(String taskId) {
        // TODO(zj): to be implemented
        if (StringUtils.isEmpty(taskId)) {
            return false;
        }
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
