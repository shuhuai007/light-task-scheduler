package com.github.ltsopensource.jobtracker.channel;

import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.core.commons.utils.Assert;
import com.github.ltsopensource.core.logger.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unit tests for {@link ChannelManager}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ChannelWrapper.class, Logger.class})
public class ChannelManagerTest {
    private static final String JOB_CLIENT_NODE_GROUP_NAME = "test_client";
    private static final String TASK_TRACKER_NODE_GROUP_NAME = "test_tasktracker";
    private static final String JOB_CLIENT_IDENTITY = "XXXXXX";
    private static final String TASK_TRACKER_IDENTITY = "YYYYYY";

    private ChannelManager channelManager;
    private Logger mockLogger = PowerMockito.mock(Logger.class);

    @Before
    public void before() {
        channelManager = new ChannelManager();
        Whitebox.setInternalState(channelManager, "LOGGER", mockLogger);

        channelManager.start();
    }

    @After
    public void after() {
        channelManager.stop();
    }

    @Test
    public void getChannelsTest() {
        List<ChannelWrapper> channelList = channelManager.getChannels(JOB_CLIENT_NODE_GROUP_NAME,
                NodeType.JOB_CLIENT);
        Assert.isNull(channelList);

        channelList = channelManager.getChannels(TASK_TRACKER_NODE_GROUP_NAME,
                NodeType.TASK_TRACKER);
        Assert.isNull(channelList);
    }

    @Test
    public void getChannelTest() {
        ChannelWrapper channel = channelManager.getChannel(JOB_CLIENT_NODE_GROUP_NAME,
                NodeType.JOB_CLIENT, JOB_CLIENT_IDENTITY);
        Assert.isNull(channel);
        channel = channelManager.getChannel(TASK_TRACKER_NODE_GROUP_NAME,
                NodeType.TASK_TRACKER, TASK_TRACKER_IDENTITY);
        Assert.isNull(channel);
    }

    @Test
    public void offerChannelWithClientChannelTest() {
        ChannelWrapper clientChannelWrapper = PowerMockito.mock(ChannelWrapper.class);
        Mockito.when(clientChannelWrapper.getNodeGroup()).thenReturn(JOB_CLIENT_NODE_GROUP_NAME);
        Mockito.when(clientChannelWrapper.getNodeType()).thenReturn(NodeType.JOB_CLIENT);
        Mockito.when(clientChannelWrapper.getIdentity()).thenReturn(JOB_CLIENT_IDENTITY);
        Mockito.when(clientChannelWrapper.isClosed()).thenReturn(false);

        channelManager.offerChannel(clientChannelWrapper);
        ChannelWrapper actualClientChannel = channelManager.getChannel(JOB_CLIENT_NODE_GROUP_NAME,
                NodeType.JOB_CLIENT, JOB_CLIENT_IDENTITY);
        Assert.isTrue(actualClientChannel != null);

        ChannelWrapper actualTaskTrackerChannel = channelManager.getChannel(
                TASK_TRACKER_NODE_GROUP_NAME, NodeType.TASK_TRACKER, TASK_TRACKER_IDENTITY);
        Assert.isNull(actualTaskTrackerChannel);
    }

    @Test
    public void offerChannelWithTaskTrackerChannelTest() {
        ChannelWrapper taskTrackerChannelWrapper = PowerMockito.mock(ChannelWrapper.class);
        Mockito.when(taskTrackerChannelWrapper.getNodeGroup())
                .thenReturn(TASK_TRACKER_NODE_GROUP_NAME);
        Mockito.when(taskTrackerChannelWrapper.getNodeType()).thenReturn(NodeType.TASK_TRACKER);
        Mockito.when(taskTrackerChannelWrapper.getIdentity()).thenReturn(TASK_TRACKER_IDENTITY);
        Mockito.when(taskTrackerChannelWrapper.isClosed()).thenReturn(false);

        ConcurrentHashMap offlineTaskTrackerMap = new ConcurrentHashMap<String, Long>();
        offlineTaskTrackerMap.put(TASK_TRACKER_IDENTITY, 10000000L);
        Whitebox.setInternalState(channelManager, "offlineTaskTrackerMap", offlineTaskTrackerMap);

        channelManager.offerChannel(taskTrackerChannelWrapper);
        ChannelWrapper actualTaskTrackerChannel = channelManager.getChannel(
                TASK_TRACKER_NODE_GROUP_NAME, NodeType.TASK_TRACKER, TASK_TRACKER_IDENTITY);
        Assert.isTrue(actualTaskTrackerChannel != null);

        Assert.isTrue(!offlineTaskTrackerMap.containsKey(TASK_TRACKER_IDENTITY));
    }
}
