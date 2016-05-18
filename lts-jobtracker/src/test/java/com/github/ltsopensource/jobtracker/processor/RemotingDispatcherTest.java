package com.github.ltsopensource.jobtracker.processor;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.concurrent.limiter.RateLimiter;
import com.github.ltsopensource.core.protocol.JobProtos;
import com.github.ltsopensource.jobtracker.channel.ChannelManager;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.remoting.Channel;
import com.github.ltsopensource.remoting.protocol.RemotingCommand;
import com.github.ltsopensource.remoting.protocol.RemotingProtos;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.TimeUnit;

/**
 * Tests for {@link RemotingDispatcher}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RemotingDispatcher.class})
public class RemotingDispatcherTest {
    private RemotingDispatcher mRemotingDispatcher;
    private JobTrackerAppContext jobTrackerAppContext;

    @Before
    public void before() {
        jobTrackerAppContext = new JobTrackerAppContext();
        Config config = new Config();
        config.setWorkThreads(10);
        config.setIdentity("fjdaslfjlasj");
        jobTrackerAppContext.setConfig(config);
        jobTrackerAppContext.setChannelManager(new ChannelManager());
        mRemotingDispatcher = new RemotingDispatcher(jobTrackerAppContext);
    }

    @Test
    public void processRequestWithHeartBeatRequestTest() throws Exception {
        Channel channel = PowerMockito.mock(Channel.class);
        RemotingCommand request = PowerMockito.mock(RemotingCommand.class);
        Mockito.when(request.getCode()).thenReturn(JobProtos.RequestCode.HEART_BEAT.code());

        RemotingDispatcher remotingDispatcher = PowerMockito.spy(new RemotingDispatcher
                (jobTrackerAppContext));
        PowerMockito.doNothing().when(remotingDispatcher, "offerHandler", channel, request);
        RemotingCommand responseCommand = remotingDispatcher.processRequest(channel, request);
        PowerMockito.verifyPrivate(remotingDispatcher).invoke("offerHandler", channel, request);
        Assert.assertEquals(JobProtos.ResponseCode.HEART_BEAT_SUCCESS.code(), responseCommand
                .getCode());
    }

    @Test
    public void processRequestWithNotReqLimitTest() throws Exception {
        Channel channel = PowerMockito.mock(Channel.class);
        RemotingCommand request = PowerMockito.mock(RemotingCommand.class);
        Mockito.when(request.getCode()).thenReturn(JobProtos.RequestCode.JOB_PULL.code());


        RemotingDispatcher remotingDispatcher = PowerMockito.spy(new RemotingDispatcher
                (jobTrackerAppContext));
        RemotingCommand responseCommand = RemotingCommand.createResponseCommand(RemotingProtos
                .ResponseCode.SUCCESS.code());

        PowerMockito.doReturn(responseCommand).when(remotingDispatcher, "doBiz", channel, request);
        Whitebox.setInternalState(remotingDispatcher, "reqLimitEnable", false);

        remotingDispatcher.processRequest(channel, request);
        PowerMockito.verifyPrivate(remotingDispatcher).invoke("doBiz", channel, request);
        PowerMockito.verifyPrivate(remotingDispatcher, Mockito.never())
                .invoke("doBizWithReqLimit", channel, request);
    }

    @Test
    public void processRequestWithReqLimitTest() throws Exception {
        Channel channel = PowerMockito.mock(Channel.class);
        RemotingCommand request = PowerMockito.mock(RemotingCommand.class);
        Mockito.when(request.getCode()).thenReturn(JobProtos.RequestCode.JOB_PULL.code());

        RemotingDispatcher remotingDispatcher = PowerMockito.spy(new RemotingDispatcher
                (jobTrackerAppContext));

        Whitebox.setInternalState(remotingDispatcher, "reqLimitEnable", true);
        RateLimiter rateLimiter = PowerMockito.mock(RateLimiter.class);
        Mockito.when(rateLimiter.tryAcquire(Mockito.anyInt(), Mockito.eq(TimeUnit.MILLISECONDS)))
                .thenReturn(false);
        Whitebox.setInternalState(remotingDispatcher, "rateLimiter", rateLimiter);

        RemotingCommand responseCommand = remotingDispatcher.processRequest(channel, request);
        Assert.assertEquals(RemotingProtos.ResponseCode.SYSTEM_BUSY.code(),
                responseCommand.getCode());
    }

}
