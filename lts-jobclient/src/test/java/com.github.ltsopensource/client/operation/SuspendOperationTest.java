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
 * Unit tests for {@link SuspendOperation}.
 */
public class SuspendOperationTest {

    private SuspendOperation suspendOperation;
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
        suspendOperation = new SuspendOperation(taskId, "", "", "", "");
    }

    @Test
    public void generateHttpCommandTest() {
        HttpCmd command = suspendOperation.generateHttpCommand();
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_SUSPEND_LTS_TASK, command.getCommand());
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_SUSPEND_OPERATION_TASK_ID));
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_SUSPEND_OPERATION_TASK_TRACKER_GROUP_NAME));
    }

}
