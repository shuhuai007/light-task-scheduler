package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.cmd.HttpCmdProc;
import com.github.ltsopensource.cmd.HttpCmdRequest;
import com.github.ltsopensource.cmd.HttpCmdResponse;

/**
 * 一些系统配置更改CMD.
 */
public class SysConfigModifyHttpCmd implements HttpCmdProc {

    @Override
    public String nodeIdentity() {
        return null;
    }

    @Override
    public String getCommand() {
        return null;
    }

    @Override
    public HttpCmdResponse execute(HttpCmdRequest request) throws Exception {
        return null;
    }
}
