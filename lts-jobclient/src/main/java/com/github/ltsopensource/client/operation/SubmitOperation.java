package com.github.ltsopensource.client.operation;

import com.github.ltsopensource.client.domain.LTSTask;
import com.github.ltsopensource.cmd.DefaultHttpCmd;
import com.github.ltsopensource.cmd.HttpCmd;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.core.cmd.HttpCmdParamNames;
import com.github.ltsopensource.core.json.JSON;

/**
 * Operation used to submit the {@link LTSTask} to the jobTracker.
 */
public class SubmitOperation extends Operation{
    private LTSTask ltsTask;

    public SubmitOperation(LTSTask ltsTask) {
        this.ltsTask = ltsTask;
    }

    public HttpCmd generateHttpCommand() {
        HttpCmd httpCmd = new DefaultHttpCmd();
        httpCmd.setCommand(HttpCmdNames.HTTP_CMD_SUBMIT_LTS_TASK);
        httpCmd.addParam(HttpCmdParamNames.PARAM_KEY_FOR_SUBMIT_OPERATION,
                JSON.toJSONString(ltsTask));
        return httpCmd;
    }
}
