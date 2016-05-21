package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.client.jdl.JDLObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link JDLParser}.
 */
public class JDLParserTest {

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

        jdl =
        "{" +
                "\"engine\":\"lts\"" + "," +
                "\"taskName\":\"test_task\"" + "," +
                "\"depends\":[\"100\", \"200\"]" + "," +
                "\"coordinator\":{" +
                    "\"frequency\"" + ":" + "\"10 * * * * ?\"" + "," +
                    "\"start\""     + ":" + "\"2016-01-07T24:00Z\"" + "," +
                    "\"end\""       + ":" + "\"2016-01-08T23:00Z\"" + "," +
                    "\"controls\""  + ":" + "{" +
                            "\"timeout\":\"-1\"," +
                            "\"concurrency\":1," +
                            "\"execution\":\"FIFO\"," +
                            "\"throttle\":3 " +
                    "}" +
                 "}" + "," +

                "\"workflow\"" + ":" + "{" +
                    "\"start\"" + ":" + "\"start_node_name\"" + "," +
                    "\"fork\"" + ":" + "[" +
                      "{" +
                           "\"name\":" + "\"fork_node1\"," +
                           "\"paths\":" + "[\"node2\"," + "\"node3\"]," +
                           "\"join\":" + "[{\"name\":\"node4\", \"to\":\"join_node_name\"}]" +
                       "}" +
                   "]" + "," +
                   "\"jobs\":" + "[" +
                        "{" +
                            "\"type\":" +"\"shell\"" + "," +
                            "\"name\":" +"\"test\"" + "," +
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
                            "\"ok\":" + "\"next_node\"" + "," +
                            "\"error\":" + "\"fail\"" +
                        "}" +
                   "]" +
               "}"  +
        "}";
        jdlObject = JDLParser.parse(jdl);
        Assert.assertEquals("lts", jdlObject.getEngine());
        Assert.assertEquals("test_task", jdlObject.getTaskName());
        Assert.assertEquals(2, jdlObject.getDepends().size());
        Assert.assertEquals("10 * * * * ?", jdlObject.getCoordinator().getFrequency());
        Assert.assertEquals("2016-01-07T24:00Z", jdlObject.getCoordinator().getStart());
        Assert.assertEquals("2016-01-08T23:00Z", jdlObject.getCoordinator().getEnd());
        Assert.assertEquals(new Integer(-1), jdlObject.getCoordinator().getControls()
                .getTimeout());
        Assert.assertEquals(new Integer(1), jdlObject.getCoordinator().getControls()
                .getConcurrency());
        Assert.assertEquals("FIFO", jdlObject.getCoordinator().getControls()
                .getExecution());
        Assert.assertEquals(new Integer(3), jdlObject.getCoordinator().getControls()
                .getThrottle());
        Assert.assertEquals("start_node_name", jdlObject.getWorkflow().getStart());
        Assert.assertEquals(1, jdlObject.getWorkflow().getFork().size());
        Assert.assertEquals("fork_node1", jdlObject.getWorkflow().getFork().get(0).getName());
        Assert.assertEquals(2, jdlObject.getWorkflow().getFork().get(0).getPaths().size());
        Assert.assertEquals(1, jdlObject.getWorkflow().getFork().get(0).getJoin().size());
        Assert.assertEquals("node4", jdlObject.getWorkflow().getFork().get(0).getJoin().get(0)
                .getName());
        Assert.assertEquals("join_node_name", jdlObject.getWorkflow().getFork().get(0).getJoin().get(0)
                .getTo());
        Assert.assertEquals(1, jdlObject.getWorkflow().getJobs().size());
        Assert.assertEquals("shell", jdlObject.getWorkflow().getJobs().get(0).getType());
        Assert.assertEquals("test", jdlObject.getWorkflow().getJobs().get(0).getName());
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
        Assert.assertEquals("next_node", jdlObject.getWorkflow().getJobs().get(0).getOK());
        Assert.assertEquals("fail", jdlObject.getWorkflow().getJobs().get(0).getError());
    }
}
