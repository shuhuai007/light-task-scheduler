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
public class ReRunOperationTest {

    private ReRunOperation reRunOperation;
    private String taskId;

    @Before
    public void before() {
        taskId = "1";
        reRunOperation = new ReRunOperation(taskId, new Date().getTime(), "", "", "");
    }

    @Test
    public void generateHttpCommandTest() {
        HttpCmd command = reRunOperation.generateHttpCommand();
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_RERUN_LTS_TASK, command.getCommand());
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_RERUN_OPERATION_TASK_ID));
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_RERUN_OPERATION_PLAN_TIME));
    }

}
