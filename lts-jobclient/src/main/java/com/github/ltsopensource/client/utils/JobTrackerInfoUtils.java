package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.cluster.Node;
import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.core.registry.NodeRegistryUtils;
import com.github.ltsopensource.zookeeper.ZkClient;
import com.github.ltsopensource.zookeeper.zkclient.ZkClientZkClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for getting jobTracker node list from zookeeper.
 */
public class JobTrackerInfoUtils {

    /**
     * Get jobTracker node list from zookeeper
     * @param zookeeperIP ip points to zookeeper
     * @param zookeeperPort port of zookeeper server
     * @param clusterName cluster name
     * @return a list of jobTracker
     */
    public static List<Node> getJobTrackerList(String zookeeperIP, String zookeeperPort, String
            clusterName) {
        List<Node> jobTrackerList = new ArrayList<Node>();
        Config config = new Config();
        config.setRegistryAddress("zookeeper://" + zookeeperIP + ":" + zookeeperPort);
        ZkClient zkClient = new ZkClientZkClient(config);

        String jobTrackerGroupPath = NodeRegistryUtils.getNodeTypePath(clusterName, NodeType.JOB_TRACKER);

        List<String> childList = zkClient.getChildren(jobTrackerGroupPath);
        for(String child : childList) {
            Node node = NodeRegistryUtils.parse(jobTrackerGroupPath + "/" + child);
            jobTrackerList.add(node);
        }

        zkClient.close();
        return jobTrackerList;
    }
}
