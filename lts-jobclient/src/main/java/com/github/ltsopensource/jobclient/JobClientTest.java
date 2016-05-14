package com.github.ltsopensource.jobclient;

import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.jobclient.domain.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JobClientTest {
    public JobClientTest() {
    }

    public static void main(String[] args) throws IOException {
//        submitWidthReplaceOnExist();
//        submitWorkflow();
        submitCoordinator();
    }

    private static void submitCoordinator() {
        JobClient jobClient = startJobClient();
        // workflowStaticId comes from database.
        String workflowStaticId = "1";
        // The cron task will be assigned an instanceId when being submitted.
        String submitInstanceId = StringUtils.generateUUID();
        // all the jobs has the same cron expression
        String cronExpr = "10 * * * * ?";
        Job job1 = createJob("job_cron_1");
        job1.setParam("workflowStaticId", workflowStaticId);
        job1.setParam("submitInstanceId", submitInstanceId);
        job1.setCronExpression(cronExpr);
        Job job2 = createJob("job_cron_2");
        job2.setParam("workflowStaticId", workflowStaticId);
        job2.setParam("submitInstanceId", submitInstanceId);
        job2.setCronExpression(cronExpr);

        job1.setParam("parents", "");
        job2.setParam("parents", job1.getTaskId());

        List<Job> dag = new ArrayList<Job>();
        dag.add(job1);
        dag.add(job2);
        Response response = jobClient.submitJob(dag);
        System.out.println(response);

    }

    private static void submitWidthReplaceOnExist() {
        RetryJobClient jobClient = startJobClient();
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

    public static void submitWorkflow() throws IOException {
        RetryJobClient jobClient = startJobClient();
        String wfInstanceId = JobUtils.generateJobId();
        Job job1 = createJob("wf_t_1");
        Job job2 = createJob("wf_t_2");
        Job job3 = createJob("wf_t_3");
        Job job4 = createJob("wf_t_4");
        job1.setParam("parents", "");
        job1.setParam("wfInstanceId", wfInstanceId);
        job2.setParam("parents", job1.getTaskId());
        job2.setParam("wfInstanceId", wfInstanceId);
        job3.setParam("parents", job2.getTaskId());
        job3.setParam("wfInstanceId", wfInstanceId);
        job4.setParam("parents", job1.getTaskId());
        job4.setParam("wfInstanceId", wfInstanceId);

        List<Job> dag = new ArrayList<Job>();
        dag.add(job1);
        dag.add(job2);
        dag.add(job3);
        dag.add(job4);
        Response response = jobClient.submitJob(dag);
        System.out.println(response);
    }

    private static Job createJob(String taskID) {
        Job job = new Job();
        job.setTaskId(taskID);
        job.setParam("shopId", "1122222221");
        job.setTaskTrackerNodeGroup("test_trade_taskTracker");
        job.setNeedFeedback(true);
        job.setReplaceOnExist(true);
        return job;
    }

    private static RetryJobClient startJobClient() {
        RetryJobClient jobClient = new RetryJobClient();
        jobClient.setNodeGroup("test_jobClient");
        jobClient.setClusterName("test_cluster");
        jobClient.setRegistryAddress("zookeeper://127.0.0.1:2181");
        jobClient.start();
        return jobClient;
    }


}
