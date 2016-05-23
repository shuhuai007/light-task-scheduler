package com.github.ltsopensource.client.jdl;

import com.github.ltsopensource.kv.cache.DataCache;

import java.beans.beancontext.BeanContextMembershipEvent;
import java.util.List;

/**
 * Represent workflow info of {@link JDLObject}.
 */
public class WorkflowObject {
    private List<String> start;
    private List<ForkObject> fork;
    private List<JobObject> jobs;

    public List<String> getStart() {
        return start;
    }

    public void setStart(List<String> start) {
        this.start = start;
    }

    public void setFork(List<ForkObject> fork) {
        this.fork = fork;
    }

    public List<ForkObject> getFork() {
        return fork;

    }


    public List<JobObject> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobObject> jobs) {
        this.jobs = jobs;
    }
}
