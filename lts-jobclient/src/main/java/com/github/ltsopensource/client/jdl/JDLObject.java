package com.github.ltsopensource.client.jdl;

import javax.swing.*;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a jdl json.
 */
public class JDLObject implements Serializable {

    private String engine;
    private String taskName;
    private List<String> depends;
    private CoordinatorObject coordinator;
    private WorkflowObject workflow;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getDepends() {
        return depends;
    }

    public void setDepends(List<String> depends) {
        this.depends = depends;
    }

    public CoordinatorObject getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(CoordinatorObject coordinator) {
        this.coordinator = coordinator;
    }

    public WorkflowObject getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowObject workflow) {
        this.workflow = workflow;
    }
}
