package com.github.ltsopensource.jobtracker.domain;

import com.github.ltsopensource.jobtracker.channel.ChannelWrapper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskTracker状态对象.
 */
public class TaskTrackerNode {
    // 节点组名称
    public String nodeGroup;
    // 可用线程数
    public AtomicInteger availableThread;
    // 唯一标识
    public String identity;
    // 该节点的channel
    public ChannelWrapper channel;

    public Long timestamp = null;

    private String ip;
    private Integer httpPort;

    /**
     * Constructs new {@link TaskTrackerNode}.
     *
     * @param identity identity name
     */
    public TaskTrackerNode(String identity) {
        this.identity = identity;
    }

    /**
     * Constructs new {@link TaskTrackerNode}.
     *
     * @param identity identity name
     * @param nodeGroup node group name
     */
    public TaskTrackerNode(String identity, String nodeGroup) {
        this.nodeGroup = nodeGroup;
        this.identity = identity;
    }

    /**
     * Constructs new {@TaskTrackerNode}.
     *
     * @param nodeGroup node group name
     * @param availableThread available thread counts
     * @param identity identity name
     * @param channel channel wrapper object
     */
    public TaskTrackerNode(String nodeGroup, int availableThread, String identity, ChannelWrapper channel) {
        this.nodeGroup = nodeGroup;
        this.availableThread = new AtomicInteger(availableThread);
        this.identity = identity;
        this.channel = channel;
    }

    /**
     * Gets ip of taskTracker node.
     *
     * @return ip of this node
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip for the taskTracker node.
     *
     * @param ip ip to be set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets http port of the taskTracker node.
     *
     * @return http port this node is listening
     */
    public Integer getHttpPort() {
        return httpPort;
    }

    /**
     * Sets http port for the taskTracker node.
     *
     * @param httpPort http port this node is listening
     */
    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * Gets node group.
     *
     * @return node group name
     */
    public String getNodeGroup() {
        return nodeGroup;
    }

    /**
     * Sets node group.
     *
     * @param nodeGroup node group name
     */
    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    /**
     * Gets available thread count.
     *
     * @return thread counts of available for this taskTracker
     */
    public AtomicInteger getAvailableThread() {
        return availableThread;
    }

    /**
     * Sets available thread count.
     *
     * @param availableThread thread counts of available for this taskTracker
     */
    public void setAvailableThread(int availableThread) {
        this.availableThread = new AtomicInteger(availableThread);
    }

    /**
     * Gets identity.
     *
     * @return identity name
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets identity.
     *
     * @param identity identity name
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * Gets channel info.
     *
     * @return channel wrapper
     */
    public ChannelWrapper getChannel() {
        return channel;
    }

    /**
     * Sets channel info.
     *
     * @param channel channel wrapper
     */
    public void setChannel(ChannelWrapper channel) {
        this.channel = channel;
    }

    /**
     * Gets timestamp of last updates.
     *
     * @return timestamp of last updates
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp timestamp to be set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskTrackerNode)) {
            return false;
        }

        TaskTrackerNode that = (TaskTrackerNode) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TaskTrackerNode{"
                + "nodeGroup='" + nodeGroup + '\''
                + ", availableThread=" + (availableThread == null ? 0 : availableThread.get())
                + ", identity='" + identity + '\''
                + ", channel=" + channel
                + '}';
    }
}
