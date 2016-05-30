package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.biz.logger.JobLogUtils;
import com.github.ltsopensource.biz.logger.domain.LogType;
import com.github.ltsopensource.biz.logger.domain.WorkflowLogStatus;
import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.commons.utils.LogUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobQueueType;
import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.CronJobQueue;
import com.github.ltsopensource.queue.ExecutableJobQueue;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.SuspendJobQueue;
import com.github.ltsopensource.queue.WaitingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.List;
import java.util.Map;

/**
 * HTTP command to resume a suspended lts task.
 */
public class ResumeLTSTaskHttpCmd implements HttpCmdProc {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeLTSTaskHttpCmd.class);

    private JobTrackerAppContext appContext;

    /**
     * Creates new {@link ResumeLTSTaskHttpCmd}.
     *
     * @param appContext jobTracker app context
     */
    public ResumeLTSTaskHttpCmd(JobTrackerAppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public String nodeIdentity() {
        return appContext.getConfig().getIdentity();
    }

    @Override
    public String getCommand() {
        return HttpCmdNames.HTTP_CMD_RESUME_LTS_TASK;
    }

    @Override
    public HttpCmdResponse execute(HttpCmdRequest request) throws Exception {
        LogUtils.logMethod(LOGGER, Level.INFO, "enter");
        HttpCmdResponse response = new HttpCmdResponse();
        response.setSuccess(false);

        String taskId = request.getParam(HttpCmdParamNames.PARAM_KEY_FOR_RESUME_OPERATION_TASK_ID);
        String taskTrackerGroupName = request.getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_RESUME_OPERATION_TASK_TRACKER_GROUP_NAME);
        if (StringUtils.isEmpty(taskId)) {
            response.setMsg("taskId can not be null");
            return response;
        }
        if (StringUtils.isEmpty(taskTrackerGroupName)) {
            response.setMsg("taskTrackerGroupName can not be null");
            return response;
        }

        try {
            resume(taskId, taskTrackerGroupName, appContext);
            LOGGER.info("Resume lts task succeed, taskId:{}, taskTrackerGroupName:{}", taskId, taskTrackerGroupName);
            response.setSuccess(true);

        } catch (Exception e) {
            LOGGER.error("Resume lts task error, message:", e);
            response.setMsg("Suspend lts task error, message:" + e.getMessage());
        }
        return response;
    }

    private void resume(String workflowId, String taskTrackerGroupName, JobTrackerAppContext appContext) {
        final CronJobQueue cronJobQueue = appContext.getCronJobQueue();
        final WaitingJobQueue waitingJobQueue = appContext.getWaitingJobQueue();
        final ExecutableJobQueue executableJobQueue = appContext.getExecutableJobQueue();
        final ExecutingJobQueue executingJobQueue = appContext.getExecutingJobQueue();
        final SuspendJobQueue suspendJobQueue = appContext.getSuspendJobQueue();

        List<JobPo> jobPoList = appContext.getSuspendJobQueue().getJobsByWorkflowId(workflowId);
        for (JobPo jobPo : jobPoList) {
            JobQueueType originQueue =  JobQueueType.valueOf(jobPo.getInternalExtParam(JobInfoConstants
                    .JOB_PO_INTERNAL_PARAM_SUSPEND_ORIGIN_KEY));
            removeOriginKeyFromInternalParam(jobPo);
            switch (originQueue) {
                case CRON_JOB_QUEUE:
                    recoverCronQueue(jobPo, cronJobQueue, suspendJobQueue);
                    break;
                case WAITING_JOB_QUEUE:
                    recoverWaitingQueue(jobPo, waitingJobQueue, suspendJobQueue);
                    break;
                case EXECUTABLE_JOB_QUEUE:
                    recoverExecutableQueue(jobPo, executableJobQueue, suspendJobQueue);
                    break;
                case EXECUTING_JOB_QUEUE:
                    recoverExecutingQueue(jobPo, executingJobQueue, suspendJobQueue);
                    break;
                default:
                    break;
            }
        }
    }

    private void recoverExecutingQueue(JobPo jobPo, ExecutingJobQueue executingJobQueue,
                                       SuspendJobQueue suspendJobQueue) {
        suspendJobQueue.remove(jobPo.getJobId());
        executingJobQueue.add(jobPo);
        JobLogUtils.log(LogType.RESUME, jobPo, WorkflowLogStatus.RUNNING, appContext.getJobLogger());
    }

    private void recoverExecutableQueue(JobPo jobPo, ExecutableJobQueue executableJobQueue,
                                        SuspendJobQueue suspendJobQueue) {
        suspendJobQueue.remove(jobPo.getJobId());
        executableJobQueue.add(jobPo);
        JobLogUtils.log(LogType.RESUME, jobPo, WorkflowLogStatus.RUNNING, appContext.getJobLogger());
    }

    private void recoverWaitingQueue(JobPo jobPo, WaitingJobQueue waitingJobQueue, SuspendJobQueue suspendJobQueue) {
        suspendJobQueue.remove(jobPo.getJobId());
        waitingJobQueue.add(jobPo);
        JobLogUtils.log(LogType.RESUME, jobPo, WorkflowLogStatus.RUNNING, appContext.getJobLogger());
    }

    private void recoverCronQueue(JobPo jobPo, CronJobQueue cronJobQueue, SuspendJobQueue suspendJobQueue) {
        suspendJobQueue.remove(jobPo.getJobId());
        cronJobQueue.add(jobPo);
        JobLogUtils.log(LogType.RESUME, jobPo, WorkflowLogStatus.RUNNING, appContext.getJobLogger());
    }

    private void removeOriginKeyFromInternalParam(JobPo jobPo) {
        Map<String, String> internalExtParams = jobPo.getInternalExtParams();
        if (internalExtParams.containsKey(JobInfoConstants.JOB_PO_INTERNAL_PARAM_SUSPEND_ORIGIN_KEY)) {
            internalExtParams.remove(JobInfoConstants.JOB_PO_INTERNAL_PARAM_SUSPEND_ORIGIN_KEY);
        }
    }
}
