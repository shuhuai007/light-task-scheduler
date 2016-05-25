package com.github.ltsopensource.core.support;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.JobGeneratorUtils;
import com.github.ltsopensource.core.domain.JobType;
import com.github.ltsopensource.queue.domain.JobPo;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link JobDomainConverter}.
 */
public class JobDomainConverterTest {
    private Job realTimeJob;
    private Job triggerTimeJob;
    private Job cronJob;

    @Before
    public void before() {
        realTimeJob = JobGeneratorUtils.createRealTimeJob();
        triggerTimeJob = JobGeneratorUtils.createTriggerTimeJob();
        cronJob = JobGeneratorUtils.createCronJob();
    }

    @Test
    public void convertRealTimeJob2JobPo() {
        JobPo jobPo = JobDomainConverter.convert(realTimeJob);
        testCommonFields4JobPo(jobPo, realTimeJob);

        // test cron expression for real time job.
        Assert.assertTrue(StringUtils.isEmpty(jobPo.getCronExpression()));

        // test start time and end time for real time job.
        Assert.assertNull(jobPo.getStartTime());
        Assert.assertNull(jobPo.getEndTime());
        // test trigger time for real time job.
        Assert.assertNotNull(jobPo.getTriggerTime());
        Assert.assertEquals(realTimeJob.getTriggerTime(), jobPo.getTriggerTime());
        // test job type.
        Assert.assertEquals(JobType.REAL_TIME, jobPo.getJobType());
    }

    @Test
    public void convertTriggerTimeJob2JobPo() {
        JobPo jobPo = JobDomainConverter.convert(triggerTimeJob);
        testCommonFields4JobPo(jobPo, triggerTimeJob);

        // test cron expression for trigger time job.
        Assert.assertTrue(StringUtils.isEmpty(jobPo.getCronExpression()));

        // test start time and end time for trigger time job.
        Assert.assertNull(jobPo.getStartTime());
        Assert.assertNull(jobPo.getEndTime());
        // test trigger time for trigger time job.
        Assert.assertNotNull(triggerTimeJob.getTriggerTime());
        Assert.assertEquals(triggerTimeJob.getTriggerTime(), jobPo.getTriggerTime());
        // test job type.
        Assert.assertEquals(JobType.TRIGGER_TIME, jobPo.getJobType());
    }

    @Test
    public void convertCronJob2JobPo() {
        JobPo jobPo = JobDomainConverter.convert(cronJob);
        testCommonFields4JobPo(jobPo, cronJob);

        // test cron expression for cron job.
        Assert.assertEquals(cronJob.getCronExpression(), jobPo.getCronExpression());
        // test start time and end time for cron job.
        Assert.assertNotNull(jobPo.getStartTime());
        Assert.assertEquals(cronJob.getStartTime(), jobPo.getStartTime());
        Assert.assertNotNull(jobPo.getEndTime());
        Assert.assertEquals(cronJob.getEndTime(), jobPo.getEndTime());
        // test trigger time for cron job.
        Assert.assertNull(cronJob.getTriggerTime());
        Assert.assertEquals(cronJob.getTriggerTime(), jobPo.getTriggerTime());
        // test job type.
        Assert.assertEquals(JobType.CRON, jobPo.getJobType());
        // test job internal ext param.
        Assert.assertEquals(cronJob.getExtParams().size(), jobPo.getExtParams().size());
        Assert.assertTrue(jobPo.getInternalExtParams().size() > 0);
    }

    private void testCommonFields4JobPo(JobPo jobPo, Job expectedJob) {
        Assert.assertEquals(expectedJob.getSubmitTime(), jobPo.getSubmitTime());
        Assert.assertEquals(expectedJob.getTaskTrackerNodeGroup(), jobPo.getTaskTrackerNodeGroup());
        Assert.assertEquals(expectedJob.getWorkflowId(), jobPo.getWorkflowId());
        Assert.assertEquals(expectedJob.getWorkflowName(), jobPo.getWorkflowName());
        Assert.assertEquals(StringUtils.join(expectedJob.getWorkflowDepends(),
                JobInfoConstants.JOB_PO_WORKFLOW_DEPENDS_SEPARATOR), jobPo.getWorkflowDepends());

        Assert.assertEquals(expectedJob.getJobName(), jobPo.getJobName());
        Assert.assertEquals(expectedJob.getJobNodeType(), jobPo.getJobNodeType());
        Assert.assertEquals(expectedJob.getMaxRetryTimes(), jobPo.getMaxRetryTimes().intValue());
        Assert.assertEquals(expectedJob.getRetryInternal(), jobPo.getRetryInternal());
        Assert.assertEquals(expectedJob.getTaskId(), jobPo.getTaskId());
        Assert.assertEquals(expectedJob.getPriority(), jobPo.getPriority());
        Assert.assertEquals(expectedJob.getRepeatCount(), jobPo.getRepeatCount().intValue());
        Assert.assertEquals(expectedJob.getRepeatInterval(), jobPo.getRepeatInterval());
        Assert.assertTrue(StringUtils.isEmpty(jobPo.getSubmitNodeGroup()));
        Assert.assertEquals(expectedJob.getSubmitNodeGroup(), jobPo.getSubmitNodeGroup());
    }

    @Test
    public void convertJobLog4JobPoTest() {
        JobPo jobPo = JobDomainConverter.convert(cronJob);
        jobPo.setLastGenerateTriggerTime(SystemClock.now());
        JobLogPo jobLogPo = JobDomainConverter.convert2JobLog(jobPo);
        Assert.assertNotNull(jobLogPo.getGmtCreated());
        Assert.assertEquals(jobPo.getJobType(), jobLogPo.getJobType());
        Assert.assertEquals(jobPo.getTaskTrackerIdentity(), jobLogPo.getTaskTrackerIdentity());
        Assert.assertEquals(jobPo.getJobId(), jobLogPo.getJobId());
        Assert.assertEquals(jobPo.getTaskId(), jobLogPo.getTaskId());
        Assert.assertEquals(jobPo.getRealTaskId(), jobLogPo.getRealTaskId());
        Assert.assertEquals(jobPo.getPriority(), jobLogPo.getPriority());
        Assert.assertEquals(jobPo.getSubmitNodeGroup(), jobLogPo.getSubmitNodeGroup());
        Assert.assertEquals(jobPo.getTaskTrackerNodeGroup(), jobLogPo.getTaskTrackerNodeGroup());
        Assert.assertEquals(jobPo.getExtParams(), jobLogPo.getExtParams());
        Assert.assertEquals(jobPo.getInternalExtParams(), jobLogPo.getInternalExtParams());
        Assert.assertEquals(jobPo.isNeedFeedback(), jobLogPo.isNeedFeedback());
        Assert.assertEquals(jobPo.getCronExpression(), jobLogPo.getCronExpression());
        Assert.assertEquals(jobPo.getTriggerTime(), jobLogPo.getTriggerTime());
        Assert.assertEquals(jobPo.getRetryTimes(), jobLogPo.getRetryTimes());
        Assert.assertEquals(jobPo.getMaxRetryTimes(), jobLogPo.getMaxRetryTimes());
        Assert.assertEquals(jobPo.getRepeatCount(), jobLogPo.getRepeatCount());
        Assert.assertEquals(jobPo.getRepeatedCount(), jobLogPo.getRepeatedCount());
        Assert.assertEquals(jobPo.getRepeatInterval(), jobLogPo.getRepeatInterval());
        Assert.assertEquals(jobPo.getRelyOnPrevCycle(), jobLogPo.getDepPreCycle());
        Assert.assertEquals(jobPo.getLastGenerateTriggerTime(), jobLogPo.getLastGenerateTriggerTime());
        Assert.assertEquals(jobPo.getSubmitTime(), jobLogPo.getSubmitTime());
        Assert.assertEquals(jobPo.getWorkflowId(), jobLogPo.getWorkflowId());
        Assert.assertEquals(jobPo.getWorkflowName(), jobLogPo.getWorkflowName());
        Assert.assertEquals(jobPo.getWorkflowDepends(), jobLogPo.getWorkflowDepends());
        Assert.assertEquals(jobPo.getStartTime(), jobLogPo.getStartTime());
        Assert.assertEquals(jobPo.getEndTime(), jobLogPo.getEndTime());
        Assert.assertEquals(jobPo.getJobName(), jobLogPo.getJobName());
        Assert.assertEquals(jobPo.getJobNodeType(), jobLogPo.getJobNodeType());
        Assert.assertEquals(jobPo.getRetryInternal(), jobLogPo.getRetryInternal());
    }

}
