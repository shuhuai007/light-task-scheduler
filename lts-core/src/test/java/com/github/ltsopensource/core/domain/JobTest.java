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


    private Job realTimeJob;
    private Job triggerTimeJob;
    private Job cronJob;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        realTimeJob = JobGeneratorUtils.createRealTimeJob();
        triggerTimeJob = JobGeneratorUtils.createTriggerTimeJob();
        cronJob = JobGeneratorUtils.createCronJob();
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
}
