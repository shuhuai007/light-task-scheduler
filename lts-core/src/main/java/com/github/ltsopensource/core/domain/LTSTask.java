package com.github.ltsopensource.core.domain;

import com.github.ltsopensource.core.json.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * LTS task definition.
 */
public class LTSTask implements Serializable  {
    private List<Job> dag = new ArrayList<Job>();

    public LTSTask add(Job job) {
        dag.add(job);
        return this;
    }

    public List<Job> getDag() {
        return dag;
    }

    public void setDag(List<Job> dag) {
        this.dag = dag;
    }

    public LTSTask addList(List<Job> jobList) {
        for (Job job : jobList) {
            add(job);
        }
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Job getStart() {
        if (dag != null && !dag.isEmpty()) {
            return dag.get(0);
        } else {
            return null;
        }
    }

    public Job getEnd() {
        if (dag != null && !dag.isEmpty()) {
            return dag.get(dag.size() - 1);
        } else {
            return null;
        }
    }
}
