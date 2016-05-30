package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

/**
 * Unit tests for {@link ReRunLTSTaskHttpCmd}.
 */
public class ReRunLTSTaskHttpCmdTest {

    @Test
    public void nodeIdentityTest() {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        Config config = new Config();
        config.setIdentity("asdfasfasdfasdf");
        PowerMockito.doReturn(config).when(jobTrackerAppContext).getConfig();
        ReRunLTSTaskHttpCmd cmd = new ReRunLTSTaskHttpCmd(jobTrackerAppContext);
        Assert.assertNotNull(cmd.nodeIdentity());
        Assert.assertEquals("asdfasfasdfasdf", cmd.nodeIdentity());
    }

    @Test
    public void getCommandTest() {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        ReRunLTSTaskHttpCmd cmd = new ReRunLTSTaskHttpCmd(jobTrackerAppContext);
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_RERUN_LTS_TASK, cmd.getCommand());
    }

}
