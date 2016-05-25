package com.github.ltsopensource.client;

import com.github.ltsopensource.client.operation.SubmitOperation;
import com.github.ltsopensource.client.utils.JDLParser;
import com.github.ltsopensource.core.domain.LTSTask;
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
@PrepareForTest({LTSClient.class, JDLParser.class})
public class LTSClientTest {

    private static final String JOB_TRACKER_URL = "127.0.0.1:8080";
    private LTSClient ltsClient;


    @Rule
    ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        ltsClient = new LTSClient(JOB_TRACKER_URL);
    }

    @Test
    public void submitWithExceptionTest() throws Exception {
        String JDL = "{\"taskName\": sdf }";
        String taskId = "1";
        String taskTrackGroupName = "test";
        ltsClient = PowerMockito.spy(new LTSClient(JOB_TRACKER_URL));
        PowerMockito.mockStatic(JDLParser.class);
        PowerMockito.when(JDLParser.verifyJDL(JDL)).thenReturn(false);

        thrown.expect(LTSClientException.class);
        ltsClient.submit(JDL, taskId, taskTrackGroupName);
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

    @Test
    public void submitWithSuccessTest() throws Exception {
        String realTimeJDL =
            "{" +
                    "\"engine\":\"lts\"" + "," +
                    "\"taskName\":\"test_task\"" + "," +
                    "\"depends\":[\"100\", \"200\"]" + "," +
                    "\"coordinator\":{" +
                        "\"start\""     + ":" + "\"2016-01-07T17:15:44.000Z\"" + "," +
                        "\"controls\""  + ":" + "{" +
                                "\"timeout\":\"-1\"," +
                                "\"concurrency\":1," +
                                "\"execution\":\"FIFO\"," +
                                "\"throttle\":3 " +
                        "}" +
                     "}" + "," +

                    "\"workflow\"" + ":" + "{" +
                        "\"start\"" + ":" + "[\"node1\"]" + "," +
                        "\"fork\"" + ":" + "[" +
                          "{" +
                               "\"name\":" + "\"fork_node1\"," +
                               "\"paths\":" + "[\"node2\"," + "\"node3\"]," +
                               "\"join\":" + "{\"name\":\"node4\", \"to\":\"join_node_name\"}" +
                           "}" +
                       "]" + "," +
                       "\"jobs\":" + "[" +
                            "{" +
                                "\"type\":" +"\"shell\"" + "," +
                                "\"name\":" +"\"node1\"" + "," +
                                "\"retryMax\":" +"2" + "," +
                                "\"retryInterval\":" +"1" + "," +
                                "\"prepare\":" +"\"this is prepare operation\"" + "," +
                                "\"decision\":" +"[]" + "," +
                                "\"configuration\":" +"[" +
                                    "{\"name\":\"key1\",\"value\":\"value1\"}" + "," +
                                    "{\"name\":\"key2\",\"value\":\"value2\"}" +
                                "]" + "," +
                                "\"exec\":" + "\"echo good morning\"" + "," +
                                "\"files\":" + "[\"f1\",\"f2\",\"f3\"]" + "," +
                                "\"arguments\":" + "[\"a1\", \"a2\", \"a3\"]" + "," +
                                "\"ok\":" + "\"node2\"" + "," +
                                "\"error\":" + "\"fail\"" +
                            "}" + "," +
                            "{" +
                                "\"type\":" +"\"shell\"" + "," +
                                "\"name\":" +"\"node2\"" + "," +
                                "\"retryMax\":" +"2" + "," +
                                "\"retryInterval\":" +"1" + "," +
                                "\"prepare\":" +"\"this is prepare operation\"" + "," +
                                "\"decision\":" +"[]" + "," +
                                "\"configuration\":" +"[" +
                                    "{\"name\":\"key1\",\"value\":\"value1\"}" + "," +
                                    "{\"name\":\"key2\",\"value\":\"value2\"}" +
                                "]" + "," +
                                "\"exec\":" + "\"echo good morning\"" + "," +
                                "\"files\":" + "[\"f1\",\"f2\",\"f3\"]" + "," +
                                "\"arguments\":" + "[\"a1\", \"a2\", \"a3\"]" + "," +
                                "\"ok\":" + "\"end\"" + "," +
                                "\"error\":" + "\"fail\"" +
                            "}" +
                       "]" +
                    "}"  +
            "}";
        String taskId = "1";
        String taskTrackGroupName = "test_trade_taskTracker";
        ltsClient = new LTSClient("127.0.0.1","2181", "test_cluster");
        ltsClient.submit(realTimeJDL, taskId, taskTrackGroupName);
    }
}
