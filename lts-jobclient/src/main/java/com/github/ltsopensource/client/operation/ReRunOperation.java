package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.cmd.DefaultHttpCmd;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;

/**
 * Operation to re-run a ltsTask which belongs to certain plan time.
 */
public class ReRunOperation extends Operation {
    private String taskId;
    private Long planTime;

    /**
     * Constructs new {@link ReRunOperation}.
     *
     * @param taskId task id of the lts task
     * @param utcPlanTime plan time
     * @param zookeeperIP zookeeper ip address
     * @param zookeeperPort zookeeper port
     * @param clusterName name of cluster
     */
    public ReRunOperation(String taskId, Long utcPlanTime, String zookeeperIP, String zookeeperPort, String
            clusterName) {
        super(zookeeperIP, zookeeperPort, clusterName);
        this.taskId = taskId;
        this.planTime = utcPlanTime;
    }

    @Override
    public HttpCmd generateHttpCommand() {
        HttpCmd httpCmd = new DefaultHttpCmd();
        httpCmd.setCommand(HttpCmdNames.HTTP_CMD_RERUN_LTS_TASK);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_RERUN_OPERATION_TASK_ID, taskId);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_RERUN_OPERATION_PLAN_TIME,
                String.valueOf(planTime));
        return httpCmd;
    }
}
