package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.client.LTSClientException;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.cmd.HttpCmdClient;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cluster.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class of all the operations, such as {@link SubmitOperation},etc.
 */
public abstract class Operation {
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
            throw new LTSClientException("No http response");
        }
    }

    protected List<Node> getJobTrackerNodeList() {
        // TODO (zj): to be implemented
        List<Node> jobTrackerNodeList = new ArrayList<Node>();
        Node jobTrackerNode = new Node();
        jobTrackerNode.setIdentity("JT_192.168.14.152_23542_16-25-53.983");
        jobTrackerNode.setIp("192.168.14.152");
        jobTrackerNode.setHttpCmdPort(8719);
        jobTrackerNodeList.add(jobTrackerNode);
        return jobTrackerNodeList;
    }

    public abstract HttpCmd generateHttpCommand();
}
