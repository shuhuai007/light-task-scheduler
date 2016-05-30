package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.commons.utils.LogUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;

import java.util.Date;

/**
 * HTTP command to re-run a lts task based on certain plan time.
 */
public class ReRunLTSTaskHttpCmd implements HttpCmdProc {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReRunLTSTaskHttpCmd.class);

    private JobTrackerAppContext appContext;

    public ReRunLTSTaskHttpCmd(JobTrackerAppContext jobTrackerAppContext) {
        appContext = jobTrackerAppContext;
    }

    @Override
    public String nodeIdentity() {
        return appContext.getConfig().getIdentity();
    }

    @Override
    public String getCommand() {
        return HttpCmdNames.HTTP_CMD_RERUN_LTS_TASK;
    }

    @Override
    public HttpCmdResponse execute(HttpCmdRequest request) throws Exception {
        LogUtils.logMethod(LOGGER, Level.INFO, "enter");
        HttpCmdResponse response = new HttpCmdResponse();
        response.setSuccess(false);

        String taskId = request.getParam(HttpCmdParamNames.PARAM_KEY_FOR_RERUN_OPERATION_TASK_ID);
        String planTime = request.getParam(
                HttpCmdParamNames.PARAM_KEY_FOR_RERUN_OPERATION_PLAN_TIME);
        if (StringUtils.isEmpty(taskId)) {
            response.setMsg("taskId can not be null");
            return response;
        }
        if (StringUtils.isEmpty(planTime)) {
            response.setMsg("planTime can not be null");
            return response;
        }

        try {
            reRun(taskId, Long.valueOf(planTime), appContext);
            LOGGER.info("ReRun lts task succeed, taskId:{}, planTime:{}", taskId, planTime);
            response.setSuccess(true);

        } catch (Exception e) {
            LOGGER.error("ReRun lts task error, message:", e);
            response.setMsg("ReRun lts task error, message:" + e.getMessage());
        }
        return response;
    }

    private void reRun(String workflowId, Long planTime, JobTrackerAppContext appContext) {
        // TODO(zj): to be implemented

    }
}
