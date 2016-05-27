package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.domain.Job;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Unit tests for {@link KillOperation}.
 */
public class KillOperationTest {

    private KillOperation killOperation;
    private String taskId;

    @Before
    public void before() {
        Job job = new Job();
        job.setTaskId("test_taskid");
        job.setParam("shopId", "1122222221");
        job.setTaskTrackerNodeGroup("taskgroup");
        job.setNeedFeedback(false);
        job.setReplaceOnExist(true);
        job.setParam("cronStartTime", String.valueOf(new Date().getTime()));
        killOperation = new KillOperation(taskId, "", "", "", "");
    }

    @Test
    public void generateHttpCommandTest() {
        HttpCmd command = killOperation.generateHttpCommand();
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_KILL_LTS_TASK, command.getCommand());
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_ID));
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_TRACKER_GROUP_NAME));
    }

}
