package com.github.ltsopensource.core.domain;

import com.github.ltsopensource.core.commons.utils.DateUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobNodeType;

import java.util.ArrayList;
import java.util.Date;

/**
 * Support class for testing.
 */
public class JobGeneratorUtils {
    private static final String TASK_TRACKER_NODE_GROUP_NAME = "test_task_tracker_group_name";
    private static final String WORKFLOW_SAMPLE_ID = "workflow_sample_id";
    private static final String WORKFLOW_SAMPLE_NAME = "workflow_sample_name";
    public static final Long TRIGGER_TIME_JOB_SAMPLE_TRIGGER_TIME = DateUtils.parseYMD_HMS
            ("2026-11-11 11:11:11").getTime();

    private static ArrayList<String> WORKFLOW_DEPENDS = new ArrayList<String>();
    static {
        WORKFLOW_DEPENDS.add("100");
        WORKFLOW_DEPENDS.add("200");
    }
    public static Job createCronJob() {
        Job cronJob = new Job();
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

        cronJob.setStartTime(new Date().getTime());
        cronJob.setEndTime(DateUtils.addDay(new Date(), 2).getTime());

        cronJob.setParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_PREPARE_KEY, "good_prepare");
        cronJob.setParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_DECISION_KEY, "good_decision");
        cronJob.setParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_CONFIGURATION_KEY,
                "key1:value1;key2:value2");
        cronJob.setParam(JobInfoConstants.JOB_PO_INTERNAL_PARAM_KEY_PREFIX + "test_param", "this " +
                "is internal param");
        return cronJob;
    }

    public static Job createTriggerTimeJob() {
        Job triggerTimeJob = new Job();
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

        triggerTimeJob.setTriggerTime(TRIGGER_TIME_JOB_SAMPLE_TRIGGER_TIME);
        return triggerTimeJob;
    }

    public static Job createRealTimeJob() {
        Job realTimeJob = new Job();
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
        return realTimeJob;
    }
}
