package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.cmd.DefaultHttpCmd;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.domain.LTSTask;
import com.github.ltsopensource.core.json.JSON;

/**
 * Operation used to kill a {@link LTSTask}.
 */
public class KillOperation extends Operation {
    private String taskTrackerGroupName;
    private String taskId;

    public KillOperation(String taskId, String taskTrackerGroupName, String zookeeperIP, String zookeeperPort, String clusterName) {
        super(zookeeperIP, zookeeperPort, clusterName);
        this.taskId = taskId;
        this.taskTrackerGroupName = taskTrackerGroupName;
    }

    @Override
    public HttpCmd generateHttpCommand() {
        HttpCmd httpCmd = new DefaultHttpCmd();
        httpCmd.setCommand(HttpCmdNames.HTTP_CMD_KILL_LTS_TASK);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_ID, taskId);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_TRACKER_GROUP_NAME,
                taskTrackerGroupName);
        return httpCmd;
    }
}
