package com.github.ltsopensource.jobtracker.domain;

import com.github.ltsopensource.jobtracker.channel.ChannelWrapper;

/**
 * 客户端节点.
 */
public class JobClientNode {

    // 节点组名称
    public String nodeGroup;
    // 唯一标识
    public String identity;
    // 该节点的channel
    public ChannelWrapper channel;

    /**
     * Constructs new {@link JobClientNode}.
     *
     * @param nodeGroup node group name
     * @param identity identity name
     * @param channel indicates a channel
     */
    public JobClientNode(String nodeGroup, String identity, ChannelWrapper channel) {
        this.nodeGroup = nodeGroup;
        this.identity = identity;
        this.channel = channel;
    }

    /**
     * Constructs new {@link JobClientNode}.
     *
     * @param identity identity name
     */
    public JobClientNode(String identity) {
        this.identity = identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobClientNode)) {
            return false;
        }

        JobClientNode that = (JobClientNode) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : 0;
    }

    /**
     * Gets node group.
     *
     * @return node group
     */
    public String getNodeGroup() {
        return nodeGroup;
    }

    /**
     * Sets node group.
     *
     * @param nodeGroup node group
     */
    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    /**
     * Gets identity.
     *
     * @return identity of node
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets identity.
     *
     * @param identity identity of node
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * Gets channel.
     *
     * @return channel wrapper
     */
    public ChannelWrapper getChannel() {
        return channel;
    }

    /**
     * Sets channel.
     *
     * @param channel channel
     */
    public void setChannel(ChannelWrapper channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "JobClientNode{"
                + "nodeGroup='" + nodeGroup + '\''
                + ", identity='" + identity + '\''
                + ", channel=" + channel
                + '}';
    }
}
