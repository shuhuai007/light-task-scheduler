package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.client.LTSClientException;
import com.github.ltsopensource.client.utils.JobTrackerInfoUtils;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.cmd.HttpCmdClient;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cluster.Node;

import java.util.List;

/**
 * A base class of all the operations, such as {@link SubmitOperation},etc.
 */
public abstract class Operation {
    private String zookeeperIP;
    private String zookeeperPort;
    private String clusterName;

    public Operation(String zookeeperIP, String zookeeperPort, String clusterName) {
        this.zookeeperIP = zookeeperIP;
        this.zookeeperPort = zookeeperPort;
        this.clusterName = clusterName;
    }

    public Void call() throws LTSClientException {
        HttpCmd httpCmd = generateHttpCommand();
        List<Node> jobTrackerNodeList = getJobTrackerNodeList();

        HttpCmdResponse response = null;

        for (Node node : jobTrackerNodeList) {
            httpCmd.setNodeIdentity(node.getIdentity());
            response = HttpCmdClient.doGet(node.getIp(), node.getHttpCmdPort(), httpCmd);
            if (response.isSuccess()) {
                return null;
            }
        }
        if (response != null) {
            throw new LTSClientException(response.getMsg());
        } else {
            throw new LTSClientException("No http response, can't find available jobTracker!");
        }
    }

    protected List<Node> getJobTrackerNodeList() {
        return JobTrackerInfoUtils.getJobTrackerList(zookeeperIP, zookeeperPort, clusterName);
    }

    public abstract HttpCmd generateHttpCommand();
}
