package com.github.ltsopensource.core.constant;

/**
 * Constants for LTS {@link com.github.ltsopensource.core.domain.Job}.
 */
public class JobInfoConstants {

    public static final String JOB_PARENTS_CHILDREN_SEPARATOR = ",";
    public static final String JOB_PARAM_DECISION_SEPARATOR = ",";
    public static final String JOB_CONFIGURATION_KEY_VALUE_SEPARATOR = ":";
    public static final String JOB_CONFIGURATION_ITEM_SEPARATOR = ";";
    public static final String JOB_FILES_SEPARATOR = ",";
    public static final String JOB_ARGUMENTS_SEPARATOR = ",";
    public static final String JOB_PO_WORKFLOW_DEPENDS_SEPARATOR = ",";

    public static final String JOB_PARAM_PARENTS_KEY = "job.parents";
    public static final String JOB_PARAM_CHILDREN_KEY = "job.children";
    public static final String JOB_PARAM_EXEC_KEY = "job.exec";
    public static final String JOB_PARAM_FILES_KEY = "job.files";
    public static final String JOB_PARAM_ARGUMENTS_KEY = "job.arguments";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_TIMEOUT_KEY =
            "coordinator.controls.timeout";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_CONCURRENCY_KEY =
            "coordinator.controls.concurrency";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_EXECUTION_KEY =
            "coordinator.controls.execution";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_THROTTLE_KEY =
            "coordinator.controls.throttle";
    public static final String JOB_PARAM_WORKFLOW_JOBS_PREPARE_KEY = "job.prepare";
    public static final String JOB_PARAM_WORKFLOW_JOBS_DECISION_KEY = "job.decision";
    public static final String JOB_PARAM_WORKFLOW_JOBS_CONFIGURATION_KEY = "job.configuration";

    public static final String WORKFLOW_JOBS_TYPE_SHELL = "shell";
    public static final String WORKFLOW_JOBS_TYPE_URL = "url";
    public static final String START_JOB_NAME = "start";
    public static final String END_JOB_NAME = "end";

    /**
     * Prefix of internal param key.
     */
    public static final String JOB_PO_INTERNAL_PARAM_KEY_PREFIX = "__LTS_";
    public static final String LTS_IGNORE_ADD_ON_EXECUTING = JOB_PO_INTERNAL_PARAM_KEY_PREFIX +
            "ignoreAddOnExecuting";
    public static final String CRON_JOB_LAST_TRIGGER_TIME_KEY = JOB_PO_INTERNAL_PARAM_KEY_PREFIX +
            "lastTriggerTime";
}
