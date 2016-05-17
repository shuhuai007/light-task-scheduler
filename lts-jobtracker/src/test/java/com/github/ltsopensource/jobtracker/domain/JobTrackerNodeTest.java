package com.github.ltsopensource.jobtracker.domain;

import com.github.ltsopensource.core.cluster.NodeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link JobTrackerNode}
 */
public class JobTrackerNodeTest {
    private JobTrackerNode jobTrackerNode;

    @Before
    public void before() {
        jobTrackerNode = new JobTrackerNode();
    }

    @Test
    public void getNodeTypeTest() {
        NodeType nodeType = jobTrackerNode.getNodeType();
        Assert.assertEquals(NodeType.JOB_TRACKER, nodeType);
    }

    @Test
    public void getListenNodeTypesTest() {
        List<NodeType> nodeTypeList = jobTrackerNode.getListenNodeTypes();
        Assert.assertEquals(4, nodeTypeList.size());

        Assert.assertTrue(nodeTypeList.contains(NodeType.JOB_CLIENT));
        Assert.assertTrue(nodeTypeList.contains(NodeType.TASK_TRACKER));
        Assert.assertTrue(nodeTypeList.contains(NodeType.JOB_TRACKER));
        Assert.assertTrue(nodeTypeList.contains(NodeType.MONITOR));
    }
}
