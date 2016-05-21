package com.github.ltsopensource.client.jdl;

import java.beans.beancontext.BeanContextMembershipEvent;
import java.util.List;

/**
 * Represent fork info of {@link WorkflowObject}.
 */
public class ForkObject {
    private String name;
    private List<String> paths;
    private List<JoinObject> join;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<JoinObject> getJoin() {
        return join;
    }

    public void setJoin(List<JoinObject> join) {
        this.join = join;
    }
}
