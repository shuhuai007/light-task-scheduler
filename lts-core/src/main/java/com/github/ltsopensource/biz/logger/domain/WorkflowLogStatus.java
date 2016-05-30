package com.github.ltsopensource.biz.logger.domain;

/**
 * Log type of workflow.
 */
public enum WorkflowLogStatus {
    END_SUCCESS,
    END_FAIL,
    END_KILL,
    SUSPEND,
    RUNNING
}
