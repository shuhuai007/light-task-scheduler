package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.biz.logger.JobLogUtils;
import com.github.ltsopensource.biz.logger.domain.LogType;
import com.github.ltsopensource.biz.logger.domain.WorkflowLogType;
import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobQueueType;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.CronJobQueue;
import com.github.ltsopensource.queue.ExecutableJobQueue;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.WaitingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;

/**
 * HTTP command to suspend a lts task.
 */
public class SuspendLTSTaskHttpCmd implements HttpCmdProc {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuspendLTSTaskHttpCmd.class);

    private JobTrackerAppContext appContext;

    /**
     * Constructs new {@link SuspendLTSTaskHttpCmd}.
     *
     * @param jobTrackerAppContext jobTracker app context
     */
    public SuspendLTSTaskHttpCmd(JobTrackerAppContext jobTrackerAppContext) {
        appContext = jobTrackerAppContext;
    }

    @Override
    public String nodeIdentity() {
        return appContext.getConfig().getIdentity();
    }

    @Override
    public String getCommand() {
        return HttpCmdNames.HTTP_CMD_SUSPEND_LTS_TASK;
    }

    @Override
    public HttpCmdResponse execute(HttpCmdRequest request) throws Exception {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getClassName() + ", method"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HttpCmdResponse response = new HttpCmdResponse();
        response.setSuccess(false);

        String taskId = request.getParam(HttpCmdParamNames.PARAM_KEY_FOR_SUSPEND_OPERATION_TASK_ID);
        String taskTrackerGroupName = request.getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_SUSPEND_OPERATION_TASK_TRACKER_GROUP_NAME);
        if (StringUtils.isEmpty(taskId)) {
            response.setMsg("taskId can not be null");
            return response;
        }
        if (StringUtils.isEmpty(taskTrackerGroupName)) {
            response.setMsg("taskTrackerGroupName can not be null");
            return response;
        }
        try {

            suspend(taskId, taskTrackerGroupName, appContext);
            LOGGER.info("Suspend lts task succeed, taskId:{}, taskTrackerGroupName:{}", taskId, taskTrackerGroupName);
            response.setSuccess(true);

        } catch (Exception e) {
            LOGGER.error("Suspend lts task error, message:", e);
            response.setMsg("Suspend lts task error, message:" + e.getMessage());
        }
        return response;
    }

    private void suspend(String taskId, String taskTrackerGroupName, JobTrackerAppContext appContext) {
        // Suspend jobs of cron queue
        suspendCronQueue(appContext.getCronJobQueue(), taskId);
        // Suspend jobs of waiting queue.
        suspendWaitingQueue(appContext.getWaitingJobQueue(), taskId);
        // Suspend jobs of executable queue.
        suspendExecutableQueue(appContext.getExecutableJobQueue(), taskId, taskTrackerGroupName);
        // Suspend jobs of executing queue, and kill related job process in the taskTracker.
        suspendExecutingQueue(appContext.getExecutingJobQueue(), taskId, taskTrackerGroupName);
    }

    private void suspendCronQueue(CronJobQueue cronJobQueue, String workflowId) {
        recordMethod();
        List<JobPo> jobPoList = cronJobQueue.getJobsByWorkflowId(workflowId);
        // At one time, at most one cron job with regular workflowId and submitTime exists in cron job queue.
        if (CollectionUtils.isNotEmpty(jobPoList) && jobPoList.size() == 1) {
            cronJobQueue.removeBatchByWorkflowId(workflowId);
            LOGGER.info("Remove cron queue based on workflowId:{}", workflowId);

            // Add this job info into suspend queue.
            addSuspendQueue(jobPoList, JobQueueType.CRON_JOB_QUEUE);
        }
    }

    private void suspendWaitingQueue(WaitingJobQueue waitingJobQueue, String workflowId) {
        recordMethod();
        List<JobPo> jobPoList = waitingJobQueue.getJobsByWorkflowId(workflowId);
        waitingJobQueue.removeBatchByWorkflowId(workflowId);

        addSuspendQueue(jobPoList, JobQueueType.WAITING_JOB_QUEUE);
        JobLogUtils.logBatch(LogType.SUSPEND, jobPoList, WorkflowLogType.SUSPEND, appContext.getJobLogger());
    }

    private void suspendExecutableQueue(ExecutableJobQueue executableJobQueue, String workflowId,
                                        String taskTrackerGroupName) {
        List<JobPo> jobPoList = null;
        try {
            jobPoList = executableJobQueue.getJobsByWorkflowId(workflowId, taskTrackerGroupName);
        } catch (Exception e) {
            LOGGER.warn("getJobsByWorkflowId(workflowId={}, taskTrackerGroupName={}) from executable queue error:{}",
                    workflowId, taskTrackerGroupName, e.getCause());
        }
        if (CollectionUtils.isNotEmpty(jobPoList)) {
            executableJobQueue.removeBatchByWorkflowId(workflowId, taskTrackerGroupName);

            addSuspendQueue(jobPoList, JobQueueType.EXECUTABLE_JOB_QUEUE);
            JobLogUtils.logBatch(LogType.SUSPEND, jobPoList, WorkflowLogType.SUSPEND, appContext.getJobLogger());
        }

    }

    private void suspendExecutingQueue(ExecutingJobQueue executingJobQueue, String workflowId,
                                       String taskTrackerGroupName) {
        recordMethod();
        List<JobPo> jobPoList = executingJobQueue.getJobsByWorkflowId(workflowId);
        executingJobQueue.removeBatchByWorkflowId(workflowId);
        addSuspendQueue(jobPoList, JobQueueType.EXECUTING_JOB_QUEUE);
        // TODO(zj): to be considered
        killRunningJob(taskTrackerGroupName);
    }

    private void killRunningJob(String taskTrackerGroupName) {
        // do nothing temporarily
    }

    private void recordMethod() {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    private void setSuspendOrigin(JobPo jobPo, JobQueueType jobQueueType) {
        jobPo.setInternalExtParam(JobInfoConstants.JOB_PO_INTERNAL_PARAM_SUSPEND_ORIGIN_KEY, jobQueueType.name());
    }

    private void addSuspendQueue(List<JobPo> jobPoList, JobQueueType executableJobQueue) {
        for (JobPo jobPo : jobPoList) {
            setSuspendOrigin(jobPo, executableJobQueue);
            appContext.getSuspendJobQueue().add(jobPo);
        }
    }
}
