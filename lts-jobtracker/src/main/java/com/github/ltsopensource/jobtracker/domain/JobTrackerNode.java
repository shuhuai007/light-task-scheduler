package com.github.ltsopensource.jobtracker.domain;

import com.github.ltsopensource.core.cluster.Node;
import com.github.ltsopensource.core.cluster.NodeType;

/**
 * JobTracker node.
 */
public class JobTrackerNode extends Node {

    /**
     * Default {@link JobTrackerNode} constructor.
     */
    public JobTrackerNode() {
        this.setNodeType(NodeType.JOB_TRACKER);
        this.addListenNodeType(NodeType.JOB_CLIENT);
        this.addListenNodeType(NodeType.TASK_TRACKER);
        this.addListenNodeType(NodeType.JOB_TRACKER);
        this.addListenNodeType(NodeType.MONITOR);
    }
}
