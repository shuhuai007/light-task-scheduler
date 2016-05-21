package com.github.ltsopensource.client.jdl;

/**
 * Represent control info of {@link CoordinatorObject}.
 */
public class ControlsObject {
    private Integer timeout;
    private Integer concurrency;
    private String execution;
    private Integer throttle;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    public Integer getThrottle() {
        return throttle;
    }

    public void setThrottle(Integer throttle) {
        this.throttle = throttle;
    }
}
