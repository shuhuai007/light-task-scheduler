package com.github.ltsopensource.jobclient;

import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.domain.Response;

import java.io.IOException;

public class JobClientTest {
    public JobClientTest() {
    }

    public static void main(String[] args) throws IOException {
        submitWidthReplaceOnExist();
    }

    public static void submitWidthReplaceOnExist() throws IOException {
        RetryJobClient jobClient = new RetryJobClient();
        jobClient.setNodeGroup("test_jobClient");
        jobClient.setClusterName("test_cluster");
        jobClient.setRegistryAddress("zookeeper://127.0.0.1:2181");
        jobClient.start();
        Job job = new Job();
        job.setTaskId("t_back2back_no_skip");
        job.setParam("shopId", "1122222221");
        job.setTaskTrackerNodeGroup("test_trade_taskTracker");
        job.setNeedFeedback(true);
        job.setReplaceOnExist(true);
        job.setCronExpression("10 * * * * ?");
        job.setRelyOnPrevCycle(true);
        Response response = jobClient.submitJob(job);
        System.out.println(response);
    }


}
