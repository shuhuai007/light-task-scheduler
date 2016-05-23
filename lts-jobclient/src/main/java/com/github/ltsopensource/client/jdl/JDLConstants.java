package com.github.ltsopensource.client.jdl;

/**
 * Constants for jdl parser.
 */
public class JDLConstants {
    public static final String COORDINATOR_CONTROLS_TIMEOUT = "coordinator.controls.timeout";
    public static final String COORDINATOR_CONTROLS_CONCURRENCY =
            "coordinator.controls.concurrency";

    public static final String COORDINATOR_CONTROLS_EXECUTION = "coordinator.controls.execution";
    public static final String COORDINATOR_CONTROLS_THROTTLE = "coordinator.controls.throttle";

    /**
     * JDL:(workflow->jobs->type:shell)
     */
    public static final String WORKFLOW_JOBS_TYPE_SHELL = "shell";
    public static final String WORKFLOW_JOBS_PREPARE = "prepare";
    public static final String WORKFLOW_JOBS_DECISION = "decision";
    public static final String WORKFLOW_JOBS_CONFIGURATION = "configuration";
    public static final String WORKFLOW_JOBS_EXEC = "exec";
}
