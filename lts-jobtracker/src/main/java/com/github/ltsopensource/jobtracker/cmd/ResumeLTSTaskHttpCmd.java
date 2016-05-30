package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;

/**
 * HTTP command to resume a suspended lts task.
 */
public class ResumeLTSTaskHttpCmd implements HttpCmdProc {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeLTSTaskHttpCmd.class);

    private JobTrackerAppContext appContext;

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
        return null;
    }
}
