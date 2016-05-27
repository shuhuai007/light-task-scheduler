package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.cmd.DefaultHttpCmd;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.domain.LTSTask;

/**
 * Operation used to kill a {@link LTSTask}.
 */
public class KillOperation extends Operation {
    private String taskId;

    public KillOperation(String taskId, String zookeeperIP, String zookeeperPort, String jobTrackerGroupName) {
        super(zookeeperIP, zookeeperPort, jobTrackerGroupName);
        this.taskId = taskId;
    }

    @Override
    public HttpCmd generateHttpCommand() {
        HttpCmd httpCmd = new DefaultHttpCmd();
        httpCmd.setCommand(HttpCmdNames.HTTP_CMD_KILL_LTS_TASK);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION, taskId);
        return httpCmd;
    }
}
