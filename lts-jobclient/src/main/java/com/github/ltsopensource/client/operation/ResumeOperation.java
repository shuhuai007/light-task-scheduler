package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.cmd.DefaultHttpCmd;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.domain.LTSTask;

/**
 * Operation used to resume a suspended {@link LTSTask}.
 */
public class ResumeOperation extends Operation{
    private String taskTrackerGroupName;
    private String taskId;

    /**
     * Constructs new {@link ResumeOperation}.
     *
     * @param taskId task id of the lts task
     * @param taskTrackerGroupName taskTracker group name of the lts task
     * @param zookeeperIP zookeeper ip address
     * @param zookeeperPort zookeeper port
     * @param clusterName name of cluster
     */
    public ResumeOperation(String taskId, String taskTrackerGroupName, String zookeeperIP, String zookeeperPort,
                           String clusterName) {
        super(zookeeperIP, zookeeperPort, clusterName);
        this.taskId = taskId;
        this.taskTrackerGroupName = taskTrackerGroupName;
    }

    @Override
    public HttpCmd generateHttpCommand() {
        HttpCmd httpCmd = new DefaultHttpCmd();
        httpCmd.setCommand(HttpCmdNames.HTTP_CMD_RESUME_LTS_TASK);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_RESUME_OPERATION_TASK_ID, taskId);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_RESUME_OPERATION_TASK_TRACKER_GROUP_NAME,
                taskTrackerGroupName);
        return httpCmd;
    }
}
