package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.LTSTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Unit tests for {@link SubmitOperation}.
 */
public class SubmitOperationTest {

    private SubmitOperation submitOperation;
    private LTSTask ltsTask;

    @Before
    public void before() {
        ltsTask = new LTSTask();
        Job job = new Job();
        job.setTaskId("test_taskid");
        job.setParam("shopId", "1122222221");
        job.setTaskTrackerNodeGroup("taskgroup");
        job.setNeedFeedback(true);
        job.setReplaceOnExist(true);
        job.setParam("cronStartTime", String.valueOf(new Date().getTime()));
        ltsTask.add(job);
        submitOperation = new SubmitOperation(ltsTask);
    }

    @Test
    public void generateHttpCommandTest() {
        HttpCmd command = submitOperation.generateHttpCommand();
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_SUBMIT_LTS_TASK, command.getCommand());
        Assert.assertTrue(command.getParams()
                .containsKey(HttpCmdParamNames.PARAM_KEY_FOR_SUBMIT_OPERATION));
    }
}
