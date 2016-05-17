package com.github.ltsopensource.jobtracker.channel;

import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.remoting.Channel;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link ChannelWrapper}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Channel.class})
public class ChannelWrapperTest {
    private static final String JOB_CLIENT_NODE_GROUP_NAME = "test_client";
    private static final String JOB_CLIENT_IDENTITY = "XXXXXX";

    @Test
    public void equalsTest() {
        Channel mockChannel = PowerMockito.mock(Channel.class);
        ChannelWrapper cw1 = new ChannelWrapper(mockChannel, NodeType.JOB_CLIENT,
                JOB_CLIENT_NODE_GROUP_NAME, JOB_CLIENT_IDENTITY);
        ChannelWrapper cw2 = new ChannelWrapper(mockChannel, NodeType.TASK_TRACKER,
                JOB_CLIENT_NODE_GROUP_NAME, JOB_CLIENT_IDENTITY);

        Assert.assertNotEquals(cw1, cw2);

        ChannelWrapper cw3 = new ChannelWrapper(mockChannel, NodeType.JOB_CLIENT,
                JOB_CLIENT_NODE_GROUP_NAME, JOB_CLIENT_IDENTITY);
        Assert.assertEquals(cw1, cw3);
    }
}
