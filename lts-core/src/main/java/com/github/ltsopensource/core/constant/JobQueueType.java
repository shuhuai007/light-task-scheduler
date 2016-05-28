package com.github.ltsopensource.core.constant;

/**
 * Job queue type.
 */
public enum JobQueueType {
    WAITING_JOB_QUEUE,
    EXECUTABLE_JOB_QUEUE,
    EXECUTING_JOB_QUEUE,
    SUSPEND_JOB_QUEUE,
    CRON_JOB_QUEUE,
    REPEAT_JOB_QUEUE
}
