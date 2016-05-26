package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.client.jdl.JDLObject;
import com.github.ltsopensource.core.commons.utils.UTCDateUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobNodeType;
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
        Assert.assertEquals(0, jdlObject.getWorkflow().getJobs().get(0)
                .getDecision().size());
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
        String testTaskTrackerGroupName = "test_taskTrackerGroup";
        LTSTask ltsTask = JDLParser.generateLTSTask(testJDL, taskId, testTaskTrackerGroupName);
        Assert.assertNotNull(ltsTask);
        Assert.assertTrue(ltsTask.getDag().size() > 1);

        // Check start job
        checkStartJob(testTaskTrackerGroupName, ltsTask);

        // Check first actual job
        checkFirstActualJob(ltsTask);

        // Check second actual job
        Assert.assertEquals("1", ltsTask.getDag().get(2).getWorkflowId());

        // Check end job
        Assert.assertNotNull(ltsTask.retrieveEndJob());
        Assert.assertEquals(JobInfoConstants.END_JOB_NAME, ltsTask.retrieveEndJob().getJobName());
        Assert.assertEquals(JobNodeType.END_JOB, ltsTask.retrieveEndJob().getJobNodeType());

        // Check dependencies: start->node1->node2->end
        Assert.assertEquals("node1", ltsTask.retrieveStartJob()
                .getParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY));
        Assert.assertEquals("node2", ltsTask.getDag().get(1)
                .getParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY));
        Assert.assertEquals("end", ltsTask.getDag().get(2)
                .getParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY));
        Assert.assertEquals("", ltsTask.retrieveEndJob()
                .getParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY));

        // Add inverse dependencies info for ltsTask
        ltsTask.reverseDependencies();
        Assert.assertEquals("", ltsTask.retrieveStartJob()
                .getParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY));
        Assert.assertEquals("start", ltsTask.getDag().get(1)
                .getParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY));
        Assert.assertEquals("node1", ltsTask.getDag().get(2)
                .getParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY));
        Assert.assertEquals("node2", ltsTask.retrieveEndJob()
                .getParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY));

    }

    private void checkFirstActualJob(LTSTask ltsTask) {
        Assert.assertEquals("1", ltsTask.getDag().get(1).getWorkflowId());
        Assert.assertNotNull(ltsTask.getDag().get(1).getSubmitTime());
        Assert.assertEquals(ltsTask.retrieveStartJob().getSubmitTime(), ltsTask.getDag().get(1)
                .getSubmitTime());
        Assert.assertEquals(JobNodeType.SHELL_JOB, ltsTask.getDag().get(1).getJobNodeType());
        Assert.assertEquals("node1", ltsTask.getDag().get(1).getJobName());
        Assert.assertEquals(2, ltsTask.getDag().get(1).getMaxRetryTimes());
        Assert.assertEquals(1, ltsTask.getDag().get(1).getRetryInternal());
        Assert.assertEquals("this is prepare operation", ltsTask.getDag().get(1).getParam
                (JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_PREPARE_KEY));
        Assert.assertEquals("", ltsTask.getDag().get(1).getParam
                (JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_DECISION_KEY));
        Assert.assertEquals(getConfigurationStr(), ltsTask.getDag().get(1)
                .getParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_CONFIGURATION_KEY));
        Assert.assertEquals("echo good morning", ltsTask.getDag().get(1).getParam
                (JobInfoConstants.JOB_PARAM_EXEC_KEY));
        Assert.assertEquals(getFilesStr(),
                ltsTask.getDag().get(1).getParam(JobInfoConstants.JOB_PARAM_FILES_KEY));
        Assert.assertEquals(getArgumentsStr(),
                ltsTask.getDag().get(1).getParam(JobInfoConstants.JOB_PARAM_ARGUMENTS_KEY));
        Assert.assertEquals("node2", ltsTask.getDag().get(1)
                .getParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY));
    }

    private void checkStartJob(String testTaskTrackerGroupName, LTSTask ltsTask) throws Exception {
        Assert.assertEquals("1", ltsTask.retrieveStartJob().getWorkflowId());
        Assert.assertNotNull(ltsTask.retrieveStartJob().getSubmitTime());
        Assert.assertEquals(testTaskTrackerGroupName,
                ltsTask.retrieveStartJob().getTaskTrackerNodeGroup());
        Assert.assertEquals(JobInfoConstants.START_JOB_NAME, ltsTask.retrieveStartJob().getJobName());
        Assert.assertEquals("test_task", ltsTask.retrieveStartJob().getWorkflowName());
        Assert.assertEquals(2, ltsTask.retrieveStartJob().getWorkflowDepends().size());
        Assert.assertEquals("10 * * * * ?", ltsTask.retrieveStartJob().getCronExpression());
        Assert.assertEquals(UTCDateUtils.getCalendar("2016-01-07T17:15:44.000Z").getTimeInMillis(),
                ltsTask.retrieveStartJob().getStartTime().longValue());
        Assert.assertEquals(UTCDateUtils.getCalendar("2016-01-08T17:15:44.000Z").getTimeInMillis(),
                ltsTask.retrieveStartJob().getEndTime().longValue());

        Assert.assertEquals("-1", ltsTask.retrieveStartJob()
                .getParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_TIMEOUT_KEY));
        Assert.assertEquals("1", ltsTask.retrieveStartJob()
                .getParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_CONCURRENCY_KEY));
        Assert.assertEquals("FIFO", ltsTask.retrieveStartJob()
                .getParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_EXECUTION_KEY));
        Assert.assertEquals("3", ltsTask.retrieveStartJob()
                .getParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_THROTTLE_KEY));

        Assert.assertEquals("node1" ,ltsTask.retrieveStartJob().getParam(JobInfoConstants
                .JOB_PARAM_CHILDREN_KEY));
        Assert.assertEquals(JobNodeType.START_JOB, ltsTask.retrieveStartJob().getJobNodeType());
        Assert.assertEquals(true, ltsTask.retrieveStartJob().isRelyOnPrevCycle());
    }

    private String getFilesStr() {
        return "f1" + JobInfoConstants.JOB_FILES_SEPARATOR +
                "f2" + JobInfoConstants.JOB_FILES_SEPARATOR + "f3";
    }

    private String getArgumentsStr() {
        return "a1" + JobInfoConstants.JOB_ARGUMENTS_SEPARATOR +
                "a2" + JobInfoConstants.JOB_ARGUMENTS_SEPARATOR + "a3";
    }

    private String getConfigurationStr() {
        return "key1" + JobInfoConstants.JOB_CONFIGURATION_KEY_VALUE_SEPARATOR +
                "value1" + JobInfoConstants.JOB_CONFIGURATION_ITEM_SEPARATOR +
                "key2" + JobInfoConstants.JOB_CONFIGURATION_KEY_VALUE_SEPARATOR + "value2";
    }
}
