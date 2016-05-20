package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.LTSTask;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.protocol.command.JobSubmitRequest;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;

import java.util.Collections;

/**
 * HTTP command to submit a lts task.
 */
public class SubmitLTSTaskHttpCmd implements HttpCmdProc {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitLTSTaskHttpCmd.class);

    private JobTrackerAppContext appContext;

    public SubmitLTSTaskHttpCmd(JobTrackerAppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public String nodeIdentity() {
        return appContext.getConfig().getIdentity();
    }

    @Override
    public String getCommand() {
        return HttpCmdNames.HTTP_CMD_SUBMIT_LTS_TASK;
    }

    @Override
    public HttpCmdResponse execute(HttpCmdRequest request) throws Exception {
        LOGGER.debug("enter SubmitLTSTaskHttpCmd");
        HttpCmdResponse response = new HttpCmdResponse();
        response.setSuccess(false);

        String ltsTaskJSON = request.getParam(HttpCmdParamNames.PARAM_KEY_FOR_SUBMIT_OPERATION);
        if (StringUtils.isEmpty(ltsTaskJSON)) {
            response.setMsg("ltsTask can not be null");
            return response;
        }
        try {
            LTSTask ltsTask = JSON.parse(ltsTaskJSON, LTSTask.class);
            if (ltsTask == null) {
                response.setMsg("ltsTask can not be null");
                return response;
            }

            JobSubmitRequest jobSubmitRequest = new JobSubmitRequest();
            jobSubmitRequest.setJobs(ltsTask.getDag());
            appContext.getJobReceiver().receive(jobSubmitRequest);

            LOGGER.info("add lts task succeed, {}", ltsTask);

            response.setSuccess(true);

        } catch (Exception e) {
            LOGGER.error("add  lts task error, message:", e);
            response.setMsg("add  lts task error, message:" + e.getMessage());
        }
        return response;
    }

}
