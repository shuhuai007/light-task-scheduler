package com.github.ltsopensource.jobtracker.channel;

import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.remoting.Channel;

/**
 * Wrapper for {@link Channel}.
 */
public class ChannelWrapper {

    private Channel channel;
    private NodeType nodeType;
    private String nodeGroup;
    // 节点的唯一标识
    private String identity;

    /**
     * Creates a new {@link ChannelWrapper}.
     *
     * @param channel object to be wrapped
     * @param nodeType node type
     * @param nodeGroup node group name
     * @param identity node identity
     */
    public ChannelWrapper(Channel channel, NodeType nodeType, String nodeGroup, String identity) {
        this.channel = channel;
        this.nodeType = nodeType;
        this.nodeGroup = nodeGroup;
        this.identity = identity;
    }

    /**
     * Gets the {@link Channel}.
     * @return channel object
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Sets the {@link Channel}.
     *
     * @param channel channel to be set
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * Gets node type.
     *
     * @return node type
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Sets node type.
     *
     * @param nodeType node type to be set
     */
    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Gets node group.
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
     * Gets identity.
     *
     * @return identity of this channel
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets identity.
     *
     * @param identity identity string to be set
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * Checks if the channel is open.
     *
     * @return true if the channel is open
     */
    public boolean isOpen() {
        return channel.isOpen();
    }

    /**
     * Checks if the channel is closed.
     *
     * @return true if the channel is closed
     */
    public boolean isClosed() {
        return channel.isClosed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChannelWrapper)) {
            return false;
        }

        ChannelWrapper that = (ChannelWrapper) o;

        if (channel != null ? !channel.equals(that.channel) : that.channel != null) {
            return false;
        }
        if (identity != null ? !identity.equals(that.identity) : that.identity != null) {
            return false;
        }
        if (nodeGroup != null ? !nodeGroup.equals(that.nodeGroup) : that.nodeGroup != null) {
            return false;
        }
        if (nodeType != that.nodeType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = channel != null ? channel.hashCode() : 0;
        result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
        result = 31 * result + (nodeGroup != null ? nodeGroup.hashCode() : 0);
        result = 31 * result + (identity != null ? identity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChannelWrapper{"
                + "channel=" + channel + ", nodeType=" + nodeType
                + ", nodeGroup='" + nodeGroup + '\'' + ", identity='" + identity + '\''
                + '}';
    }
}
