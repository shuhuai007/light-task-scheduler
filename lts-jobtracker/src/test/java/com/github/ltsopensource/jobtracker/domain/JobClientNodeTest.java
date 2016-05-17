package com.github.ltsopensource.jobtracker.domain;

import com.github.ltsopensource.jobtracker.channel.ChannelWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link JobClientNode}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ChannelWrapper.class})
public class JobClientNodeTest {
    private JobClientNode jobClientNode;
    private static final String JOB_CLIENT_NODE_GROUP_NAME = "test_client";
    private static final String JOB_CLIENT_IDENTITY = "XXXXXX";

    @Before
    public void before() {
        ChannelWrapper jobClientChannelWrapper = PowerMockito.mock(ChannelWrapper.class);
        jobClientNode = new JobClientNode(JOB_CLIENT_NODE_GROUP_NAME, JOB_CLIENT_IDENTITY,
                jobClientChannelWrapper);
    }

    @Test
    public void equalsTest() {
        ChannelWrapper jobClientChannelWrapper = PowerMockito.mock(ChannelWrapper.class);
        JobClientNode node2 = new JobClientNode("sadfasdfasdf", JOB_CLIENT_IDENTITY,
                jobClientChannelWrapper);
        Assert.assertTrue(jobClientNode.equals(node2));

        JobClientNode node3 = new JobClientNode(JOB_CLIENT_NODE_GROUP_NAME, "alsadfikxxiii",
                jobClientChannelWrapper);
        Assert.assertFalse(jobClientNode.equals(node3));
    }

    @Test
    public void hashCodeTest() {
        Assert.assertEquals(JOB_CLIENT_IDENTITY.hashCode(), jobClientNode.hashCode());
    }
}
