package com.github.ltsopensource.jobtracker.support;

import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.JobType;
import com.github.ltsopensource.core.exception.JobReceiveException;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.protocol.command.JobSubmitRequest;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.queue.WaitingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link JobReceiver}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobReceiver.class, Logger.class})
public class JobReceiverTest {
    private JobReceiver jobReceiver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        jobReceiver = new JobReceiver(new JobTrackerAppContext());
    }

    @Test
    public void receiveWithNoJobTest() throws Exception {
        JobSubmitRequest jobSubmitRequest = PowerMockito.mock(JobSubmitRequest.class);
        List<Job> jobList = PowerMockito.mock(List.class);

        jobReceiver = PowerMockito.spy(new JobReceiver(new JobTrackerAppContext()));
        PowerMockito.when(jobSubmitRequest.getJobs()).thenReturn(jobList);
        jobReceiver.receive(jobSubmitRequest);
        PowerMockito.verifyPrivate(jobReceiver, Mockito.never())
                .invoke("addToQueue", Mockito.any(Job.class), Mockito.any(JobSubmitRequest.class));
    }

    @Test
    public void receiveWithJobsTest() throws Exception {
        JobSubmitRequest jobSubmitRequest = PowerMockito.mock(JobSubmitRequest.class);
        List<Job> jobList = new ArrayList<Job>();
        Job job = PowerMockito.mock(Job.class);
        jobList.add(job);

        jobReceiver = PowerMockito.spy(new JobReceiver(new JobTrackerAppContext()));
        PowerMockito.when(jobSubmitRequest.getJobs()).thenReturn(jobList);
        thrown.expect(JobReceiveException.class);
        jobReceiver.receive(jobSubmitRequest);
    }

    @Test
    public void addJob4CronJobTest() throws Exception {
        jobReceiver = PowerMockito.spy(new JobReceiver(new JobTrackerAppContext()));
        JobPo jobPo = PowerMockito.mock(JobPo.class);
        Mockito.doReturn(JobType.CRON).when(jobPo).getJobType();
        PowerMockito.doNothing().when(jobReceiver, "addCronJob", jobPo);

        jobReceiver.addJob(jobPo);
        PowerMockito.verifyPrivate(jobReceiver).invoke("addCronJob", jobPo);
    }

    @Test
    public void addJob4RepeatJobTest() throws Exception {
        jobReceiver = PowerMockito.spy(new JobReceiver(new JobTrackerAppContext()));
        JobPo jobPo = PowerMockito.mock(JobPo.class);
        Mockito.doReturn(JobType.REPEAT).when(jobPo).getJobType();
        PowerMockito.doNothing().when(jobReceiver, "addRepeatJob", jobPo);

        jobReceiver.addJob(jobPo);
        PowerMockito.verifyPrivate(jobReceiver).invoke("addRepeatJob", jobPo);
    }

    @Test
    public void addJob4NonCronOrRepeatJobTest() throws Exception {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        jobReceiver = PowerMockito.spy(new JobReceiver(jobTrackerAppContext));
        JobPo jobPo = PowerMockito.mock(JobPo.class);
        Mockito.doReturn(JobType.REAL_TIME).when(jobPo).getJobType();
        PowerMockito.doReturn(false).when(jobReceiver, "shouldIgnore", jobPo);

        WaitingJobQueue waitingJobQueue = PowerMockito.mock(WaitingJobQueue.class);
        Mockito.when(jobTrackerAppContext.getWaitingJobQueue()).thenReturn(waitingJobQueue);
        Mockito.doReturn(true).when(waitingJobQueue).add(jobPo);

        jobReceiver.addJob(jobPo);
        Mockito.verify(jobTrackerAppContext).getWaitingJobQueue();
    }
}
