package com.github.ltsopensource.jobtracker.monitor;

import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.core.domain.monitor.JobTrackerMData;
import com.github.ltsopensource.core.domain.monitor.MData;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link JobTrackerMStatReporter}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobTrackerAppContext.class})
public class JobTrackerMStatReporterTest {
    private JobTrackerMStatReporter jobTrackerMStatReporter;
    @Before
    public void before() {
        JobTrackerAppContext appContext = PowerMockito.mock(JobTrackerAppContext.class);
        jobTrackerMStatReporter = new JobTrackerMStatReporter(appContext);
    }

    @Test
    public void getNodeTypeTest() {
        Assert.assertEquals(NodeType.JOB_TRACKER, jobTrackerMStatReporter.getNodeType());
    }

    @Test
    public void collectMDataTest() {
        jobTrackerMStatReporter.incReceiveJobNum();
        jobTrackerMStatReporter.incReceiveJobNum();
        MData mData = jobTrackerMStatReporter.collectMData();
        Assert.assertTrue(mData instanceof JobTrackerMData);
        Assert.assertEquals(2, ((JobTrackerMData) mData).getReceiveJobNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeExceptionNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeFailedNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeLaterNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeLaterNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeSuccessNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getFixExecutingJobNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getPushJobNum().intValue());


        jobTrackerMStatReporter.incReceiveJobNum();
        mData = jobTrackerMStatReporter.collectMData();
        Assert.assertTrue(mData instanceof JobTrackerMData);
        Assert.assertEquals(1, ((JobTrackerMData) mData).getReceiveJobNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeExceptionNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeFailedNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeLaterNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeLaterNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getExeSuccessNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getFixExecutingJobNum().intValue());
        Assert.assertEquals(0, ((JobTrackerMData) mData).getPushJobNum().intValue());
    }
}
