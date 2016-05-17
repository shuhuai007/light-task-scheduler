package com.github.ltsopensource.jobtracker.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link TaskTrackerNode}.
 */
public class TaskTrackerNodeTest {
    private static final String TASK_TRACKER_NODE_GROUP_NAME = "test_tasktracker";
    private static final String TASK_TRACKER_IDENTITY = "YYYYYY";

    private TaskTrackerNode taskTrackerNode;

    @Before
    public void before() {
        taskTrackerNode = new TaskTrackerNode(TASK_TRACKER_IDENTITY, TASK_TRACKER_NODE_GROUP_NAME);
    }

    @Test
    public void equalsTest() {
        TaskTrackerNode taskTrackerNode2 = new TaskTrackerNode("ZZZZzzzzzz");
        Assert.assertFalse(taskTrackerNode.equals(taskTrackerNode2));

        taskTrackerNode2 = new TaskTrackerNode(TASK_TRACKER_IDENTITY, "asdlfalsdfkasdf");
        Assert.assertTrue(taskTrackerNode.equals(taskTrackerNode2));
    }

    @Test
    public void hashCodeTest() {
        Assert.assertEquals(TASK_TRACKER_IDENTITY.hashCode(), taskTrackerNode.hashCode());
    }
}
