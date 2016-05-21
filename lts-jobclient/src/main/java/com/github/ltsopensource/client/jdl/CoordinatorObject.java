package com.github.ltsopensource.client.jdl;

import java.io.Serializable;

/**
 * Represents the coordinator info of jdl json.
 */
public class CoordinatorObject implements Serializable {

    private String frequency;
    private String start;
    private String end;
    private ControlsObject controls;

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public ControlsObject getControls() {
        return controls;
    }

    public void setControls(ControlsObject controls) {
        this.controls = controls;
    }
}
