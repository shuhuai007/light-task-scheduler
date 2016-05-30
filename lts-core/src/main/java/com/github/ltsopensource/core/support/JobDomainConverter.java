package com.github.ltsopensource.core.support;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.Constants;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.domain.*;
import com.github.ltsopensource.queue.domain.JobFeedbackPo;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.Map;
import java.util.Set;

/**
 * Job domain object converter.
 */
public class JobDomainConverter {

    private JobDomainConverter() {
    }

    /**
     * Convert {@link Job} to {@link JobPo}.
     *
     * @param job job info
     * @return JobPo object
     */
    public static JobPo convert2JobPo(Job job) {
        JobPo jobPo = new JobPo();
        jobPo.setSubmitTime(job.getSubmitTime());
        jobPo.setTaskTrackerNodeGroup(job.getTaskTrackerNodeGroup());
        jobPo.setWorkflowId(job.getWorkflowId());
        jobPo.setWorkflowName(job.getWorkflowName());
        jobPo.setWorkflowDepends(StringUtils.join(job.getWorkflowDepends
                (), JobInfoConstants.JOB_PO_WORKFLOW_DEPENDS_SEPARATOR));
        jobPo.setStartTime(job.getStartTime());
        jobPo.setEndTime(job.getEndTime());
        jobPo.setJobName(job.getJobName());
        jobPo.setJobNodeType(job.getJobNodeType());
        jobPo.setMaxRetryTimes(job.getMaxRetryTimes());
        jobPo.setRetryInternal(job.getRetryInternal());


        jobPo.setPriority(job.getPriority());
        jobPo.setTaskId(job.getTaskId());
        jobPo.setRealTaskId(jobPo.getTaskId());
        jobPo.setGmtCreated(SystemClock.now());
        jobPo.setGmtModified(jobPo.getGmtCreated());
        jobPo.setSubmitNodeGroup(job.getSubmitNodeGroup());

        if (CollectionUtils.isNotEmpty(job.getExtParams())) {
            Set<String> removeKeySet = null;
            for (Map.Entry<String, String> entry : job.getExtParams().entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(JobInfoConstants.JOB_PO_INTERNAL_PARAM_KEY_PREFIX)) {
                    jobPo.setInternalExtParam(key, entry.getValue());
                    removeKeySet = CollectionUtils.newHashSetOnNull(removeKeySet);
                    removeKeySet.add(key);
                }
            }
            if (removeKeySet != null) {
                for (String key : removeKeySet) {
                    job.getExtParams().remove(key);
                }
            }
        }

        jobPo.setJobType(job.getJobType());

        jobPo.setExtParams(job.getExtParams());
        jobPo.setNeedFeedback(job.isNeedFeedback());
        jobPo.setCronExpression(job.getCronExpression());
        jobPo.setRelyOnPrevCycle(job.isRelyOnPrevCycle());
        jobPo.setRepeatCount(job.getRepeatCount());
        // set trigger time for single period job (including realTime, triggerTime, repeat job)
        if (!jobPo.isCron()) {
            if (job.getTriggerTime() == null) {
                jobPo.setTriggerTime(SystemClock.now());
            } else {
                jobPo.setTriggerTime(job.getTriggerTime());
            }
        }
        if (job.getRepeatCount() != 0) {
            jobPo.setCronExpression(null);
            jobPo.setRepeatInterval(job.getRepeatInterval());
            jobPo.setInternalExtParam(Constants.FIRST_FIRE_TIME, String.valueOf(jobPo.getTriggerTime()));
        }
        return jobPo;
    }

    /**
     * Convert {@link JobPo} to {@link JobMeta}.
     *
     * @param jobPo JobPo object
     * @return JobMeta object
     */
    public static JobMeta convert2JobMeta(JobPo jobPo) {
        Job job = new Job();
        job.setJobType(jobPo.getJobType());
        job.setSubmitTime(jobPo.getSubmitTime());
        job.setWorkflowId(jobPo.getWorkflowId());
        job.setWorkflowName(jobPo.getWorkflowName());
        job.setWorkflowDepends(CollectionUtils.arrayToList(
                StringUtils.splitWithTrim(jobPo.getWorkflowDepends(),
                        JobInfoConstants.JOB_PO_WORKFLOW_DEPENDS_SEPARATOR)));
        job.setPriority(jobPo.getPriority());
        job.setExtParams(jobPo.getExtParams());
        job.setSubmitNodeGroup(jobPo.getSubmitNodeGroup());
        job.setTaskId(jobPo.getTaskId());
        job.setTaskTrackerNodeGroup(jobPo.getTaskTrackerNodeGroup());
        job.setNeedFeedback(jobPo.isNeedFeedback());
        job.setCronExpression(jobPo.getCronExpression());
        job.setTriggerTime(jobPo.getTriggerTime());
        job.setMaxRetryTimes(jobPo.getMaxRetryTimes() == null ? 0 : jobPo.getMaxRetryTimes());
        job.setRelyOnPrevCycle(jobPo.getRelyOnPrevCycle() == null ? true : jobPo.getRelyOnPrevCycle());
        job.setRepeatCount(jobPo.getRepeatCount());
        job.setRepeatInterval(jobPo.getRepeatInterval());
        job.setStartTime(jobPo.getStartTime());
        job.setEndTime(jobPo.getEndTime());
        job.setJobName(jobPo.getJobName());
        job.setJobNodeType(jobPo.getJobNodeType());
        job.setRetryInternal(jobPo.getRetryInternal());

        JobMeta jobMeta = new JobMeta();
        jobMeta.setJobId(jobPo.getJobId());
        jobMeta.setJob(job);
        jobMeta.setRealTaskId(jobPo.getRealTaskId());
        jobMeta.setInternalExtParams(jobPo.getInternalExtParams());
        jobMeta.setRetryTimes(jobPo.getRetryTimes() == null ? 0 : jobPo.getRetryTimes());
        jobMeta.setRepeatedCount(jobPo.getRepeatedCount());
        jobMeta.setJobType(jobPo.getJobType());
        jobMeta.setGmtCreated(SystemClock.now());
        jobMeta.setGmtModified(jobMeta.getGmtCreated());
        jobMeta.setRunning(jobPo.isRunning());
        jobMeta.setLastGenerateTriggerTime(jobPo.getLastGenerateTriggerTime());
        return jobMeta;
    }

    public static JobLogPo convert2JobLog(JobMeta jobMeta) {
        JobLogPo jobLogPo = new JobLogPo();
        jobLogPo.setGmtCreated(SystemClock.now());
        Job job = jobMeta.getJob();
        jobLogPo.setPriority(job.getPriority());
        jobLogPo.setExtParams(job.getExtParams());
        jobLogPo.setInternalExtParams(jobMeta.getInternalExtParams());
        jobLogPo.setSubmitNodeGroup(job.getSubmitNodeGroup());
        jobLogPo.setTaskId(job.getTaskId());
        jobLogPo.setJobType(jobMeta.getJobType());
        jobLogPo.setRealTaskId(jobMeta.getRealTaskId());
        jobLogPo.setTaskTrackerNodeGroup(job.getTaskTrackerNodeGroup());
        jobLogPo.setNeedFeedback(job.isNeedFeedback());
        jobLogPo.setRetryTimes(jobMeta.getRetryTimes());
        jobLogPo.setMaxRetryTimes(job.getMaxRetryTimes());
        jobLogPo.setDepPreCycle(jobMeta.getJob().isRelyOnPrevCycle());
        jobLogPo.setJobId(jobMeta.getJobId());
        jobLogPo.setCronExpression(job.getCronExpression());
        jobLogPo.setTriggerTime(job.getTriggerTime());

        jobLogPo.setRepeatCount(job.getRepeatCount());
        jobLogPo.setRepeatedCount(jobMeta.getRepeatedCount());
        jobLogPo.setRepeatInterval(job.getRepeatInterval());
        jobLogPo.setLastGenerateTriggerTime(jobMeta.getLastGenerateTriggerTime());
        jobLogPo.setTaskTrackerIdentity(jobMeta.getTaskTrackerIdentity());

        jobLogPo.setSubmitTime(job.getSubmitTime());
        jobLogPo.setWorkflowId(job.getWorkflowId());
        jobLogPo.setWorkflowName(job.getWorkflowName());
        jobLogPo.setWorkflowDepends(StringUtils.join(job.getWorkflowDepends(), JobInfoConstants
                .JOB_PO_WORKFLOW_DEPENDS_SEPARATOR));
        jobLogPo.setStartTime(job.getStartTime());
        jobLogPo.setEndTime(job.getEndTime());
        jobLogPo.setJobName(job.getJobName());
        jobLogPo.setJobNodeType(job.getJobNodeType());
        jobLogPo.setRetryInternal(job.getRetryInternal());

        return jobLogPo;
    }

    public static JobLogPo convert2JobLog(JobPo jobPo) {
        JobLogPo jobLogPo = new JobLogPo();
        jobLogPo.setGmtCreated(SystemClock.now());
        jobLogPo.setPriority(jobPo.getPriority());
        jobLogPo.setJobType(jobPo.getJobType());
        jobLogPo.setExtParams(jobPo.getExtParams());
        jobLogPo.setInternalExtParams(jobPo.getInternalExtParams());
        jobLogPo.setSubmitNodeGroup(jobPo.getSubmitNodeGroup());
        jobLogPo.setTaskId(jobPo.getTaskId());
        jobLogPo.setRealTaskId(jobPo.getRealTaskId());
        jobLogPo.setTaskTrackerNodeGroup(jobPo.getTaskTrackerNodeGroup());
        jobLogPo.setNeedFeedback(jobPo.isNeedFeedback());
        jobLogPo.setJobId(jobPo.getJobId());
        jobLogPo.setCronExpression(jobPo.getCronExpression());
        jobLogPo.setTriggerTime(jobPo.getTriggerTime());
        jobLogPo.setTaskTrackerIdentity(jobPo.getTaskTrackerIdentity());
        jobLogPo.setRetryTimes(jobPo.getRetryTimes());
        jobLogPo.setMaxRetryTimes(jobPo.getMaxRetryTimes());
        jobLogPo.setDepPreCycle(jobPo.getRelyOnPrevCycle());

        jobLogPo.setRepeatCount(jobPo.getRepeatCount());
        jobLogPo.setRepeatedCount(jobPo.getRepeatedCount());
        jobLogPo.setRepeatInterval(jobPo.getRepeatInterval());

        jobLogPo.setLastGenerateTriggerTime(jobPo.getLastGenerateTriggerTime());
        jobLogPo.setSubmitTime(jobPo.getSubmitTime());
        jobLogPo.setWorkflowId(jobPo.getWorkflowId());
        jobLogPo.setWorkflowName(jobPo.getWorkflowName());
        jobLogPo.setWorkflowDepends(jobPo.getWorkflowDepends());
        jobLogPo.setStartTime(jobPo.getStartTime());
        jobLogPo.setEndTime(jobPo.getEndTime());
        jobLogPo.setJobName(jobPo.getJobName());
        jobLogPo.setJobNodeType(jobPo.getJobNodeType());
        jobLogPo.setRetryInternal(jobPo.getRetryInternal());
        return jobLogPo;
    }

    public static JobFeedbackPo convert(JobRunResult result) {
        JobFeedbackPo jobFeedbackPo = new JobFeedbackPo();
        jobFeedbackPo.setJobRunResult(result);
        jobFeedbackPo.setId(StringUtils.generateUUID());
        jobFeedbackPo.setGmtCreated(SystemClock.now());
        return jobFeedbackPo;
    }

    public static BizLog convert2BizLog(JobLogPo jobLogPo) {
        BizLog bizLog = JobUtils.copyJobLogPo2BizLog(jobLogPo);
        return bizLog;
    }

    public static JobPo convert2JobPo(JobLogPo jobLogPo) {
        JobPo jobPo = new JobPo();
        jobPo.setGmtCreated(SystemClock.now());
        jobPo.setPriority(jobLogPo.getPriority());
        jobPo.setJobType(jobLogPo.getJobType());
        jobPo.setExtParams(jobLogPo.getExtParams());
        jobPo.setInternalExtParams(jobLogPo.getInternalExtParams());
        jobPo.setSubmitNodeGroup(jobLogPo.getSubmitNodeGroup());
        jobPo.setTaskId(jobLogPo.getTaskId());
        jobPo.setRealTaskId(jobLogPo.getRealTaskId());
        jobPo.setTaskTrackerNodeGroup(jobLogPo.getTaskTrackerNodeGroup());
        jobPo.setNeedFeedback(jobPo.isNeedFeedback());
        jobPo.setJobId(jobLogPo.getJobId());
        jobPo.setCronExpression(jobLogPo.getCronExpression());
        jobPo.setTriggerTime(jobLogPo.getTriggerTime());
        jobPo.setTaskTrackerIdentity(jobLogPo.getTaskTrackerIdentity());
        jobPo.setRetryTimes(jobLogPo.getRetryTimes());
        jobPo.setMaxRetryTimes(jobLogPo.getMaxRetryTimes());
        jobPo.setRelyOnPrevCycle(jobLogPo.getDepPreCycle());

        jobPo.setRepeatCount(jobLogPo.getRepeatCount());
        jobPo.setRepeatedCount(jobLogPo.getRepeatedCount());
        jobPo.setRepeatInterval(jobLogPo.getRepeatInterval());

        jobPo.setLastGenerateTriggerTime(jobLogPo.getLastGenerateTriggerTime());
        jobPo.setSubmitTime(jobLogPo.getSubmitTime());
        jobPo.setWorkflowId(jobLogPo.getWorkflowId());
        jobPo.setWorkflowName(jobLogPo.getWorkflowName());
        jobPo.setWorkflowDepends(jobLogPo.getWorkflowDepends());
        jobPo.setStartTime(jobLogPo.getStartTime());
        jobPo.setEndTime(jobLogPo.getEndTime());
        jobPo.setJobName(jobLogPo.getJobName());
        jobPo.setJobNodeType(jobLogPo.getJobNodeType());
        jobPo.setRetryInternal(jobLogPo.getRetryInternal());
        return jobPo;
    }
}
