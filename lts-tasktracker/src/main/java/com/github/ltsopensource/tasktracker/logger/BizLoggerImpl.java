package com.github.ltsopensource.tasktracker.logger;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.commons.utils.Callable;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.domain.BizLog;
import com.github.ltsopensource.core.domain.JobMeta;
import com.github.ltsopensource.core.exception.JobTrackerNotFoundException;
import com.github.ltsopensource.core.failstore.FailStorePathBuilder;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.protocol.JobProtos;
import com.github.ltsopensource.core.protocol.command.BizLogSendRequest;
import com.github.ltsopensource.core.protocol.command.CommandBodyWrapper;
import com.github.ltsopensource.core.remoting.RemotingClientDelegate;
import com.github.ltsopensource.core.support.JobDomainConverter;
import com.github.ltsopensource.core.support.NodeShutdownHook;
import com.github.ltsopensource.core.support.RetryScheduler;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.remoting.AsyncCallback;
import com.github.ltsopensource.remoting.ResponseFuture;
import com.github.ltsopensource.remoting.protocol.RemotingCommand;
import com.github.ltsopensource.tasktracker.domain.TaskTrackerAppContext;

import java.util.Collections;
import java.util.List;

/**
 * 业务日志记录器实现
 * 1. 业务日志会发送给JobTracker
 * 2. 也会采取Fail And Store 的方式
 *
 * @author Robert HG (254963746@qq.com) on 3/27/15.
 */
public class BizLoggerImpl extends BizLoggerAdapter implements BizLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(BizLoggerImpl.class);

    private Level level;
    private RemotingClientDelegate remotingClient;
    private TaskTrackerAppContext appContext;
    private RetryScheduler<BizLog> retryScheduler;

    public BizLoggerImpl(Level level, final RemotingClientDelegate remotingClient, TaskTrackerAppContext appContext) {
        this.level = level;
        if (this.level == null) {
            this.level = Level.INFO;
        }
        this.appContext = appContext;
        this.remotingClient = remotingClient;
        this.retryScheduler = new RetryScheduler<BizLog>(BizLogger.class.getSimpleName(), appContext, FailStorePathBuilder.getBizLoggerPath(appContext)) {
            @Override
            protected boolean isRemotingEnable() {
                return remotingClient.isServerEnable();
            }

            @Override
            protected boolean retry(List<BizLog> list) {
                return sendBizLog(list);
            }
        };
        this.retryScheduler.start();

        NodeShutdownHook.registerHook(appContext, this.getClass().getName(), new Callable() {
            @Override
            public void call() throws Exception {
                retryScheduler.stop();
            }
        });
    }

    @Override
    public void debug(String msg) {
        if (level.ordinal() <= Level.DEBUG.ordinal()) {
            sendMsg(msg);
        }
    }

    @Override
    public void info(String msg) {
        if (level.ordinal() <= Level.INFO.ordinal()) {
            sendMsg(msg);
        }
    }

    @Override
    public void error(String msg) {
        if (level.ordinal() <= Level.ERROR.ordinal()) {
            sendMsg(msg);
        }
    }

    private void sendMsg(String msg) {
        LOGGER.info("enter " + Thread.currentThread().getStackTrace()[1].getMethodName());
        LOGGER.info("..................msg:" + msg);
        BizLogSendRequest requestBody = CommandBodyWrapper.wrapper(appContext, new BizLogSendRequest());

        JobMeta jobMeta = getJobMeta();
        JobLogPo jobLogPo = JobDomainConverter.convert2JobLog(jobMeta);
        final BizLog bizLog = JobDomainConverter.convert2BizLog(jobLogPo);
        bizLog.setTaskTrackerIdentity(requestBody.getIdentity());
        bizLog.setTaskTrackerNodeGroup(requestBody.getNodeGroup());
        bizLog.setLogTime(SystemClock.now());
        bizLog.setMsg(msg);
        bizLog.setLevel(level);

        requestBody.setBizLogs(Collections.singletonList(bizLog));

        if (!remotingClient.isServerEnable()) {
            retryScheduler.inSchedule(StringUtils.generateUUID(), bizLog);
            return;
        }

        RemotingCommand request = RemotingCommand.createRequestCommand(JobProtos.RequestCode.BIZ_LOG_SEND.code(), requestBody);
        try {
            // 有可能down机，日志丢失
            remotingClient.invokeAsync(request, new AsyncCallback() {
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                    RemotingCommand response = responseFuture.getResponseCommand();

                    if (response != null && response.getCode() == JobProtos.ResponseCode.BIZ_LOG_SEND_SUCCESS.code()) {
                        // success
                    } else {
                        retryScheduler.inSchedule(StringUtils.generateUUID(), bizLog);
                    }
                }
            });
        } catch (JobTrackerNotFoundException e) {
            retryScheduler.inSchedule(StringUtils.generateUUID(), bizLog);
        }
    }

    private boolean sendBizLog(List<BizLog> bizLogs) {
        if (CollectionUtils.isEmpty(bizLogs)) {
            return true;
        }
        BizLogSendRequest requestBody = CommandBodyWrapper.wrapper(appContext, new BizLogSendRequest());
        requestBody.setBizLogs(bizLogs);

        RemotingCommand request = RemotingCommand.createRequestCommand(JobProtos.RequestCode.BIZ_LOG_SEND.code(), requestBody);
        try {
            RemotingCommand response = remotingClient.invokeSync(request);
            if (response != null && response.getCode() == JobProtos.ResponseCode.BIZ_LOG_SEND_SUCCESS.code()) {
                // success
                return true;
            }
        } catch (JobTrackerNotFoundException ignored) {
        }
        return false;
    }

}
