package com.github.ltsopensource.core.constant;

import com.github.ltsopensource.core.commons.utils.StringUtils;

/**
 * Job Node Type of one {@link com.github.ltsopensource.core.domain.Job}.
 */
public enum JobNodeType {
    START_JOB,
    FORK_JOB,
    JOIN_JOB,
    DECISION_JOB,
    END_JOB,
    SHELL_JOB,
    URL_JOB
}
