package com.github.ltsopensource.jobtracker.processor;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.LogType;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.domain.BizLog;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.protocol.JobProtos;
import com.github.ltsopensource.core.protocol.command.BizLogSendRequest;
import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.remoting.Channel;
import com.github.ltsopensource.remoting.exception.RemotingCommandException;
import com.github.ltsopensource.remoting.protocol.RemotingCommand;

import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 3/30/15.
 */
public class JobBizLogProcessor extends AbstractRemotingProcessor {
    private final Logger LOGGER = LoggerFactory.getLogger(JobBizLogProcessor.class);

    public JobBizLogProcessor(JobTrackerAppContext appContext) {
        super(appContext);
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {

        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());

        BizLogSendRequest requestBody = request.getBody();

        List<BizLog> bizLogs = requestBody.getBizLogs();
        if (CollectionUtils.isNotEmpty(bizLogs)) {
            for (BizLog bizLog : bizLogs) {
                bizLog.setGmtCreated(SystemClock.now());
                bizLog.setSuccess(true);
                bizLog.setLogType(LogType.BIZ);
                appContext.getJobLogger().log(JobUtils.copyBizLog2JobLogPo(bizLog));
            }
        }

        return RemotingCommand.createResponseCommand(JobProtos.ResponseCode.BIZ_LOG_SEND_SUCCESS.code(), "");
    }
}
