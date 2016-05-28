package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.CronJobQueue;
import com.github.ltsopensource.queue.ExecutableJobQueue;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.WaitingJobQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link KillLTSTaskHttpCmd}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({KillLTSTaskHttpCmd.class, LoggerFactory.class})
public class KillLTSTaskHttpCmdTest {
    private KillLTSTaskHttpCmd cmd;

    @Before
    public void before() {
        Logger mockLogger = PowerMockito.mock(Logger.class);
        PowerMockito.mockStatic(LoggerFactory.class);
        Mockito.when(LoggerFactory.getLogger(KillLTSTaskHttpCmd.class)).thenReturn(mockLogger);

        JobTrackerAppContext mockAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        CronJobQueue mockCronJobQueue = PowerMockito.mock(CronJobQueue.class);
        WaitingJobQueue mockWaitingQueue = PowerMockito.mock(WaitingJobQueue.class);
        ExecutableJobQueue mockExecutableQueue = PowerMockito.mock(ExecutableJobQueue.class);
        ExecutingJobQueue mockExecutingQueue = PowerMockito.mock(ExecutingJobQueue.class);

        Mockito.doReturn(mockCronJobQueue).when(mockAppContext).getCronJobQueue();
        Mockito.doReturn(mockWaitingQueue).when(mockAppContext).getWaitingJobQueue();
        Mockito.doReturn(mockExecutableQueue).when(mockAppContext).getExecutableJobQueue();
        Mockito.doReturn(mockExecutingQueue).when(mockAppContext).getExecutingJobQueue();

        cmd = PowerMockito.spy(new KillLTSTaskHttpCmd(mockAppContext));
        Whitebox.setInternalState(cmd, "appContext", mockAppContext);
    }

    @Test
    public void getCommandTest() {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        KillLTSTaskHttpCmd cmd = new KillLTSTaskHttpCmd(jobTrackerAppContext);
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_KILL_LTS_TASK, cmd.getCommand());
    }

    @Test
    public void executeWithCorrectRequestTest() throws Exception {
        HttpCmdRequest mockCmdRequest = PowerMockito.mock(HttpCmdRequest.class);
        Mockito.doReturn("11111").when(mockCmdRequest).getParam(Mockito.anyString());

        HttpCmdResponse response = cmd.execute(mockCmdRequest);
        Mockito.verify(mockCmdRequest, Mockito.times(1)).getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_ID);
        Mockito.verify(mockCmdRequest, Mockito.times(1)).getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_TRACKER_GROUP_NAME);
        PowerMockito.verifyPrivate(cmd).invoke("kill", Mockito.anyString(), Mockito.anyString(), Mockito.anyObject());

        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void executeWithEmptyRequestTest() throws Exception {
        HttpCmdRequest mockCmdRequest = PowerMockito.mock(HttpCmdRequest.class);
        Mockito.doReturn("").when(mockCmdRequest).getParam(Mockito.anyString());

        HttpCmdResponse response = cmd.execute(mockCmdRequest);
        Mockito.verify(mockCmdRequest, Mockito.times(1)).getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_ID);
        Mockito.verify(mockCmdRequest, Mockito.times(1)).getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_TRACKER_GROUP_NAME);

        Assert.assertFalse(response.isSuccess());
    }
}
