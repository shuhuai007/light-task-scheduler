package com.github.ltsopensource.jobtracker.processor;

import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.exception.JobReceiveException;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.protocol.JobProtos;
import com.github.ltsopensource.core.protocol.command.CommandBodyWrapper;
import com.github.ltsopensource.core.protocol.command.JobSubmitRequest;
import com.github.ltsopensource.core.protocol.command.JobSubmitResponse;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.jobtracker.support.JobReceiver;
import com.github.ltsopensource.remoting.Channel;
import com.github.ltsopensource.remoting.exception.RemotingCommandException;
import com.github.ltsopensource.remoting.protocol.RemotingCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

/**
 * Unit tests for {@link JobSubmitProcessor}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobTrackerAppContext.class, Logger.class})
public class JobSubmitProcessorTest {
    private Channel mockChannel;
    private RemotingCommand mockRequest;
    private JobReceiver mockJobReceiver;
    private CommandBodyWrapper mockCommandBodyWrapper;
    private JobSubmitResponse jobSubmitResponse;
    private JobTrackerAppContext mockAppContext;
    private Logger mockLogger;
    private JobSubmitProcessor jobSubmitProcessor;
    private JobSubmitRequest mockJobSubmitRequest;

    @Before
    public void before() {
        mockLogger = PowerMockito.mock(Logger.class);
        mockChannel = PowerMockito.mock(Channel.class);
        mockRequest = PowerMockito.mock(RemotingCommand.class);
        mockAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        mockCommandBodyWrapper = PowerMockito.mock(CommandBodyWrapper.class);
        jobSubmitResponse = new JobSubmitResponse();
        mockJobReceiver = PowerMockito.mock(JobReceiver.class);

        mockJobSubmitRequest = PowerMockito.mock(JobSubmitRequest.class);

        Mockito.when(mockAppContext.getCommandBodyWrapper()).thenReturn(mockCommandBodyWrapper);
        Mockito.when(mockAppContext.getJobReceiver()).thenReturn(mockJobReceiver);
        Mockito.when(mockRequest.getBody()).thenReturn(mockJobSubmitRequest);
        Mockito.when(mockJobSubmitRequest.getJobs()).thenReturn(new ArrayList<Job>());

        jobSubmitProcessor = new JobSubmitProcessor(mockAppContext);
        Whitebox.setInternalState(jobSubmitProcessor, "LOGGER", mockLogger);

        Mockito.when(mockCommandBodyWrapper.wrapper(Mockito.<JobSubmitResponse>any()))
                .thenReturn(jobSubmitResponse);
    }

    @Test
    public void processRequestSuccessTest() throws RemotingCommandException, JobReceiveException {

        Mockito.doNothing().when(mockJobReceiver).receive(Mockito.<JobSubmitRequest>any());

        RemotingCommand actualRemotingCommand = jobSubmitProcessor.processRequest(mockChannel,
                mockRequest);
        Assert.assertEquals(JobProtos.ResponseCode.JOB_RECEIVE_SUCCESS.code(),
                actualRemotingCommand.getCode());

        Assert.assertEquals(jobSubmitResponse, actualRemotingCommand.getBody());
    }

    @Test
    public void processRequestFailTest() throws RemotingCommandException, JobReceiveException {
        JobReceiveException mockException = new JobReceiveException();
        Mockito.doThrow(mockException).when(mockJobReceiver).receive(Mockito.<JobSubmitRequest>any());

        Mockito.doNothing().when(mockLogger).error("Receive job failed , jobs = ", mockException);
        RemotingCommand actualRemotingCommand = jobSubmitProcessor.processRequest(mockChannel,
                mockRequest);
        Assert.assertEquals(JobProtos.ResponseCode.JOB_RECEIVE_FAILED.code(),
                actualRemotingCommand.getCode());

        Assert.assertEquals(jobSubmitResponse, actualRemotingCommand.getBody());
    }
}
