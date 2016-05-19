package com.github.ltsopensource.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link LTSClient}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LTSClient.class})
public class LTSClientTest {

    private static final String JOBTRACKER_URL = "127.0.0.1:8080";
    private LTSClient ltsClient;

    @Rule
    ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        ltsClient = new LTSClient(JOBTRACKER_URL);
    }

    @Test
    public void submitWithCorrectJDLTest() throws Exception {
        String JDL = "{\"taskName\": \"log_processing_etl\" }";
        String taskId = "1";
        ltsClient = PowerMockito.spy(new LTSClient(JOBTRACKER_URL));
        PowerMockito.doReturn(true).when(ltsClient, "verifyJDL", JDL);

        ltsClient.submit(JDL, taskId);
    }

    @Test
    public void submitWithExceptionTest() throws Exception {
        String JDL = "{\"taskName\": sdf }";
        String taskId = "1";
        ltsClient = PowerMockito.spy(new LTSClient(JOBTRACKER_URL));
        PowerMockito.doReturn(false).when(ltsClient, "verifyJDL", JDL);
        thrown.expect(LTSClientException.class);
        ltsClient.submit(JDL, taskId);
    }

    @Test
    public void killTest() throws Exception {
        String taskId = "1";
        ltsClient.kill(taskId);
    }

    @Test
    public void suspendTest() throws Exception {
        String taskId = "1";
        ltsClient.suspend(taskId);
    }

    @Test
    public void resumeTest() throws Exception {
        String taskId = "1";
        ltsClient.resume(taskId);
    }

    @Test
    public void reRunTest() throws Exception {
        String taskId = "1";
        String planTime = "2016-11-01T23:00Z";
        ltsClient.reRun(taskId, planTime);
    }

}