package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.client.jdl.JDLConstants;
import com.github.ltsopensource.client.jdl.JDLObject;
import com.github.ltsopensource.core.commons.utils.UTCDateUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.domain.LTSTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link JDLParser}.
 */
public class JDLParserTest {

    private String testJDL;

    @Before
    public void before() {
        testJDL =
            "{" +
                    "\"engine\":\"lts\"" + "," +
                    "\"taskName\":\"test_task\"" + "," +
                    "\"depends\":[\"100\", \"200\"]" + "," +
                    "\"coordinator\":{" +
                        "\"frequency\"" + ":" + "\"10 * * * * ?\"" + "," +
                        "\"start\""     + ":" + "\"2016-01-07T17:15:44.000Z\"" + "," +
                        "\"end\""       + ":" + "\"2016-01-08T17:15:44.000Z\"" + "," +
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
    }

    @Test
    public void verifyJDLWithFalseTest() {
        String jdl = "asdfasdfasdfadf";
        Assert.assertFalse(JDLParser.verifyJDL(jdl));
    }

    @Test
    public void verifyJDLWithTrueTest() {
        String jdl = "{\"engine\":\"lts\"}";
        Assert.assertTrue(JDLParser.verifyJDL(jdl));
    }

    @Test
    public void parseTest() {
        String jdl = "{\"engine\":\"lts\"}";
        JDLObject jdlObject = JDLParser.parse(jdl);
        Assert.assertEquals("lts", jdlObject.getEngine());

        jdl = "{\"engine\":\"lts\", \"a\":\"b\"}";
        jdlObject = JDLParser.parse(jdl);
        Assert.assertEquals("lts", jdlObject.getEngine());
        Assert.assertEquals(null, jdlObject.getTaskName());

        jdlObject = JDLParser.parse(testJDL);
        Assert.assertEquals("lts", jdlObject.getEngine());
        Assert.assertEquals("test_task", jdlObject.getTaskName());
        Assert.assertEquals(2, jdlObject.getDepends().size());
        Assert.assertEquals("10 * * * * ?", jdlObject.getCoordinator().getFrequency());
        Assert.assertEquals("2016-01-07T17:15:44.000Z", jdlObject.getCoordinator().getStart());
        Assert.assertEquals("2016-01-08T17:15:44.000Z", jdlObject.getCoordinator().getEnd());
        Assert.assertEquals(new Integer(-1), jdlObject.getCoordinator().getControls()
                .getTimeout());
        Assert.assertEquals(new Integer(1), jdlObject.getCoordinator().getControls()
                .getConcurrency());
        Assert.assertEquals("FIFO", jdlObject.getCoordinator().getControls()
                .getExecution());
        Assert.assertEquals(new Integer(3), jdlObject.getCoordinator().getControls()
                .getThrottle());
        Assert.assertEquals(1, jdlObject.getWorkflow().getStart().size());
        Assert.assertEquals(1, jdlObject.getWorkflow().getFork().size());
        Assert.assertEquals("fork_node1", jdlObject.getWorkflow().getFork().get(0).getName());
        Assert.assertEquals(2, jdlObject.getWorkflow().getFork().get(0).getPaths().size());
        Assert.assertEquals("node4", jdlObject.getWorkflow().getFork().get(0).getJoin().getName());
        Assert.assertEquals("join_node_name", jdlObject.getWorkflow().getFork().get(0).getJoin()
                .getTo());
        Assert.assertEquals(2, jdlObject.getWorkflow().getJobs().size());
        Assert.assertEquals("shell", jdlObject.getWorkflow().getJobs().get(0).getType());
        Assert.assertEquals("node1", jdlObject.getWorkflow().getJobs().get(0).getName());
        Assert.assertEquals(2, jdlObject.getWorkflow().getJobs().get(0).getRetryMax());
        Assert.assertEquals(1, jdlObject.getWorkflow().getJobs().get(0).getRetryInterval());
        Assert.assertEquals("this is prepare operation", jdlObject.getWorkflow().getJobs().get(0)
                .getPrepare());
        Assert.assertEquals(2, jdlObject.getWorkflow().getJobs().get(0).getConfiguration().size());
        Assert.assertEquals("key1", jdlObject.getWorkflow().getJobs().get(0).getConfiguration()
                .get(0).getName());
        Assert.assertEquals("value1", jdlObject.getWorkflow().getJobs().get(0).getConfiguration()
                .get(0).getValue());
        Assert.assertEquals("echo good morning", jdlObject.getWorkflow().getJobs().get(0).getExec());
        Assert.assertEquals(3, jdlObject.getWorkflow().getJobs().get(0).getFiles().size());
        Assert.assertEquals(3, jdlObject.getWorkflow().getJobs().get(0).getArguments().size());
        Assert.assertEquals("node2", jdlObject.getWorkflow().getJobs().get(0).getOK());
        Assert.assertEquals("fail", jdlObject.getWorkflow().getJobs().get(0).getError());
    }

    @Test
    public void generateLTSTaskTest() throws Exception {
        String taskId = "1";
        LTSTask ltsTask = JDLParser.generateLTSTask(testJDL, taskId);
        Assert.assertNotNull(ltsTask);

        Assert.assertTrue(ltsTask.getDag().size() > 1);
        Assert.assertEquals("test_task", ltsTask.getDag().get(0).getWorkflowName());
        Assert.assertEquals(2, ltsTask.getDag().get(0).getWorkflowDepends().size());
        Assert.assertEquals("10 * * * * ?", ltsTask.getDag().get(0).getCronExpression());
        Assert.assertEquals(UTCDateUtils.getCalendar("2016-01-07T17:15:44.000Z").getTimeInMillis(),
                ltsTask.getDag().get(0).getStartTime().longValue());
        Assert.assertEquals(UTCDateUtils.getCalendar("2016-01-08T17:15:44.000Z").getTimeInMillis(),
                ltsTask.getDag().get(0).getEndTime().longValue());

        Assert.assertEquals("-1", ltsTask.getDag().get(0)
                .getParam(JDLConstants.COORDINATOR_CONTROLS_TIMEOUT));
        Assert.assertEquals("1", ltsTask.getDag().get(0)
                .getParam(JDLConstants.COORDINATOR_CONTROLS_CONCURRENCY));
        Assert.assertEquals("FIFO", ltsTask.getDag().get(0)
                .getParam(JDLConstants.COORDINATOR_CONTROLS_EXECUTION));
        Assert.assertEquals("3", ltsTask.getDag().get(0)
                .getParam(JDLConstants.COORDINATOR_CONTROLS_THROTTLE));

        Assert.assertEquals("node1" ,ltsTask.getStart().getParam(JobInfoConstants
                .JOB_CHILDREN_PARAM_KEY));

    }
}
