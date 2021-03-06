package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

/**
 * Unit tests for {@link SuspendLTSTaskHttpCmd}.
 */
public class SuspendLTSTaskHttpCmdTest {

    @Test
    public void nodeIdentityTest() {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        Config config = new Config();
        config.setIdentity("asdfasfasdfasdf");
        PowerMockito.doReturn(config).when(jobTrackerAppContext).getConfig();
        SuspendLTSTaskHttpCmd cmd = new SuspendLTSTaskHttpCmd(jobTrackerAppContext);
        Assert.assertNotNull(cmd.nodeIdentity());
        Assert.assertEquals("asdfasfasdfasdf", cmd.nodeIdentity());
    }

    @Test
    public void getCommandTest() {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        SuspendLTSTaskHttpCmd cmd = new SuspendLTSTaskHttpCmd(jobTrackerAppContext);
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_SUSPEND_LTS_TASK, cmd.getCommand());
    }
}
