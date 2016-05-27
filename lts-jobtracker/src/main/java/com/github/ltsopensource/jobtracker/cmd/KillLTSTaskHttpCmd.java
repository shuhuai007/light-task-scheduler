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
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.ExecutableJobQueue;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.WaitingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;

/**
 * HTTP command to kill a lts task.
 */
public class KillLTSTaskHttpCmd implements HttpCmdProc {

    private static final Logger LOGGER = LoggerFactory.getLogger(KillLTSTaskHttpCmd.class);

    private JobTrackerAppContext appContext;

    public KillLTSTaskHttpCmd(JobTrackerAppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public String nodeIdentity() {
        return appContext.getConfig().getIdentity();
    }

    @Override
    public String getCommand() {
        return HttpCmdNames.HTTP_CMD_KILL_LTS_TASK;
    }

    @Override
    public HttpCmdResponse execute(HttpCmdRequest request) throws Exception {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());
        HttpCmdResponse response = new HttpCmdResponse();
        response.setSuccess(false);

        String taskId = request.getParam(HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_ID);
        String taskTrackerGroupName = request.getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_KILL_OPERATION_TASK_TRACKER_GROUP_NAME);
        if (StringUtils.isEmpty(taskId)) {
            response.setMsg("taskId can not be null");
            return response;
        }
        if (StringUtils.isEmpty(taskTrackerGroupName)) {
            response.setMsg("taskTrackerGroupName can not be null");
            return response;
        }
        try {

            LOGGER.info("kill lts task succeed, taskId:{}, taskTrackerGroupName:{}", taskId, taskTrackerGroupName);
            kill(taskId, taskTrackerGroupName, appContext);
            response.setSuccess(true);

        } catch (Exception e) {
            LOGGER.error("kill lts task error, message:", e);
            response.setMsg("kill lts task error, message:" + e.getMessage());
        }
        return response;
    }

    private void kill(String taskId, String taskTrackerGroupName, JobTrackerAppContext appContext) {
        // Kill jobs of waiting queue.
        removeWaitingQueue(appContext.getWaitingJobQueue(), taskId);
        // Kill jobs of executable queue.
        removeExecutableQueue(appContext.getExecutableJobQueue(), taskId, taskTrackerGroupName);
        // Kill jobs of executing queue, and kill related job process in the taskTracker.
        removeExecutingQueue(appContext.getExecutingJobQueue(), taskId);
    }

    private void removeExecutingQueue(ExecutingJobQueue executingJobQueue, String workflowId) {
        List<JobPo> jobPoList = executingJobQueue.getJobsByWorkflowId(workflowId);
        executingJobQueue.removeBatchByWorkflowId(workflowId);

        // Kill the job process in the taskTracker remotely.
        killJobsRemotely(jobPoList);
        JobLogUtils.logBatch(LogType.KILL, jobPoList, WorkflowLogType.END_KILL, appContext.getJobLogger());
    }

    private void killJobsRemotely(List<JobPo> jobPoList) {
        // TODO(zj): to be implemented

    }

    private void removeExecutableQueue(ExecutableJobQueue executableJobQueue, String workflowId,
                                       String taskTrackerGroupName) {
        List<JobPo> jobPoList = null;
        try {
            jobPoList = executableJobQueue.getJobsByWorkflowId(workflowId, taskTrackerGroupName);
        } catch(Exception e) {
            LOGGER.warn("getJobsByWorkflowId(workflowId={}, taskTrackerGroupName={}) from executable queue error:{}",
                    workflowId, taskTrackerGroupName, e.getCause());
        }
        if (CollectionUtils.isNotEmpty(jobPoList)) {
            executableJobQueue.removeBatchByWorkflowId(workflowId, taskTrackerGroupName);
            JobLogUtils.logBatch(LogType.KILL, jobPoList, WorkflowLogType.END_KILL, appContext.getJobLogger());
        }
    }

    private void removeWaitingQueue(WaitingJobQueue waitingJobQueue, String workflowId) {
        List<JobPo> jobPoList = waitingJobQueue.getJobsByWorkflowId(workflowId);
        waitingJobQueue.removeBatchByWorkflowId(workflowId);

        JobLogUtils.logBatch(LogType.KILL, jobPoList, WorkflowLogType.END_KILL, appContext.getJobLogger());
    }

}
