package com.github.ltsopensource.jobtracker.complete.biz;

import com.github.ltsopensource.core.protocol.command.JobCompletedRequest;
import com.github.ltsopensource.remoting.protocol.RemotingCommand;

/**
 * Job completed biz.
 */
public interface JobCompletedBiz {

    /**
     * Do biz when job is completed.
     *
     * @param request job completed request
     * @return remoting command
     */
    RemotingCommand doBiz(JobCompletedRequest request);
}
