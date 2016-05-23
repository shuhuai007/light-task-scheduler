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

    public static final String JOB_PARENTS_PARAM_KEY = "job.parents";
    public static final String JOB_PARAM_CHILDREN_KEY = "job.children";
    public static final String JOB_PARAM_EXEC_KEY = "job.exec";
    public static final String JOB_PARAM_FILES_KEY = "job.files";
    public static final String JOB_PARAM_ARGUMENTS_KEY = "job.arguments";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_TIMEOUT_KEY = "coordinator.controls.timeout";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_CONCURRENCY_KEY =
            "coordinator.controls.concurrency";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_EXECUTION_KEY = "coordinator.controls.execution";
    public static final String JOB_PARAM_COORDINATOR_CONTROLS_THROTTLE_KEY = "coordinator.controls.throttle";
    public static final String JOB_PARAM_WORKFLOW_JOBS_PREPARE_KEY = "prepare";
    public static final String JOB_PARAM_WORKFLOW_JOBS_DECISION_KEY = "decision";
    public static final String JOB_PARAM_WORKFLOW_JOBS_CONFIGURATION_KEY = "configuration";

    public static final String WORKFLOW_JOBS_TYPE_SHELL = "shell";
}
