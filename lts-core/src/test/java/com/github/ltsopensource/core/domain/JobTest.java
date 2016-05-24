package com.github.ltsopensource.core.domain;

import com.github.ltsopensource.core.commons.utils.DateUtils;
import com.github.ltsopensource.core.constant.JobNodeType;
import com.github.ltsopensource.core.exception.JobSubmitException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Date;

/**
 * Unit tests for {@link Job}.
 */
public class JobTest {
    private static final String TASK_TRACKER_NODE_GROUP_NAME = "test_task_tracker_group_name";
    private static final String WORKFLOW_SAMPLE_ID = "workflow_sample_id";
    private static final String WORKFLOW_SAMPLE_NAME = "workflow_sample_name";
    private static  ArrayList<String> WORKFLOW_DEPENDS = new ArrayList<String>();
    static {
        WORKFLOW_DEPENDS.add("100");
        WORKFLOW_DEPENDS.add("200");
    }

    private Job realTimeJob;
    private Job triggerTimeJob;
    private Job cronJob;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        createRealTimeJob();
        createTriggerTimeJob();
        createCronJob();
    }

    @Test
    public void isCronTest() {
        Assert.assertFalse(realTimeJob.isCron());
        Assert.assertFalse(triggerTimeJob.isCron());
        Assert.assertTrue(cronJob.isCron());
    }

    @Test
    public void isNeedFeedbackTest() {
        Assert.assertFalse(realTimeJob.isNeedFeedback());
        Assert.assertFalse(triggerTimeJob.isNeedFeedback());
        Assert.assertFalse(cronJob.isNeedFeedback());
    }

    @Test
    public void isRelyOnPrevCycleTest() {
        Assert.assertTrue(realTimeJob.isRelyOnPrevCycle());
        Assert.assertTrue(triggerTimeJob.isRelyOnPrevCycle());
        Assert.assertTrue(cronJob.isRelyOnPrevCycle());
    }

    @Test
    public void isRepeatableTest() {
        Assert.assertFalse(realTimeJob.isRepeatable());
        Assert.assertFalse(triggerTimeJob.isRepeatable());
        Assert.assertFalse(cronJob.isRepeatable());
    }

    @Test
    public void isReplaceOnExistTest() {
        Assert.assertFalse(realTimeJob.isReplaceOnExist());
        Assert.assertFalse(triggerTimeJob.isReplaceOnExist());
        Assert.assertFalse(cronJob.isReplaceOnExist());
    }

    @Test
    public void checkFieldWithWrongCronExpressionTest() {
        thrown.expect(JobSubmitException.class);
        realTimeJob.setCronExpression("sadfasd");
        realTimeJob.checkField();
    }

    @Test
    public void checkFieldWithNullTaskTrackerNodeGroupTest() {
        thrown.expect(JobSubmitException.class);
        realTimeJob.setTaskTrackerNodeGroup(null);
        realTimeJob.checkField();
    }

    @Test
    public void checkFieldWithErrorMaxRetryTimesTest() {
        thrown.expect(JobSubmitException.class);
        realTimeJob.setMaxRetryTimes(-1);
        realTimeJob.checkField();
    }

    @Test
    public void checkFieldWithErrorRepeatCountTest() {
        thrown.expect(JobSubmitException.class);
        realTimeJob.setRepeatCount(-2);
        realTimeJob.checkField();
    }

    private void createCronJob() {
        cronJob = new Job();
        cronJob.setSubmitTime(new Date().getTime());
        cronJob.setCronExpression("10 * * * * ?");
        cronJob.setJobName("cron_job");
        cronJob.setJobNodeType(JobNodeType.SHELL_JOB);
        cronJob.setTaskTrackerNodeGroup(TASK_TRACKER_NODE_GROUP_NAME);
        cronJob.setWorkflowId(WORKFLOW_SAMPLE_ID);
        cronJob.setWorkflowName(WORKFLOW_SAMPLE_NAME);
        cronJob.setWorkflowDepends(WORKFLOW_DEPENDS);
        cronJob.setMaxRetryTimes(3);
        cronJob.setRetryInternal(1);
    }

    private void createTriggerTimeJob() {
        triggerTimeJob = new Job();
        triggerTimeJob.setSubmitTime(new Date().getTime());
        triggerTimeJob.setCronExpression("");
        triggerTimeJob.setJobName("trigger_time_job");
        triggerTimeJob.setJobNodeType(JobNodeType.SHELL_JOB);
        triggerTimeJob.setTaskTrackerNodeGroup(TASK_TRACKER_NODE_GROUP_NAME);
        triggerTimeJob.setWorkflowId(WORKFLOW_SAMPLE_ID);
        triggerTimeJob.setWorkflowName(WORKFLOW_SAMPLE_NAME);
        triggerTimeJob.setWorkflowDepends(WORKFLOW_DEPENDS);
        triggerTimeJob.setMaxRetryTimes(3);
        triggerTimeJob.setRetryInternal(1);

        triggerTimeJob.setTriggerTime(DateUtils.addDay(new Date(), 1).getTime());
    }

    private void createRealTimeJob() {
        realTimeJob = new Job();
        realTimeJob.setSubmitTime(new Date().getTime());
        realTimeJob.setCronExpression("");
        realTimeJob.setJobName("real_time_job");
        realTimeJob.setJobNodeType(JobNodeType.SHELL_JOB);
        realTimeJob.setTaskTrackerNodeGroup(TASK_TRACKER_NODE_GROUP_NAME);
        realTimeJob.setWorkflowId(WORKFLOW_SAMPLE_ID);
        realTimeJob.setWorkflowName(WORKFLOW_SAMPLE_NAME);
        realTimeJob.setWorkflowDepends(WORKFLOW_DEPENDS);
        realTimeJob.setMaxRetryTimes(3);
        realTimeJob.setRetryInternal(1);
    }
}
