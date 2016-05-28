package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;

/**
 * HTTP command to suspend a lts task.
 */
public class SuspendLTSTaskHttpCmd implements HttpCmdProc {
    private JobTrackerAppContext appContext;
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
        return null;
    }
}
