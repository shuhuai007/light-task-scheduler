package com.github.ltsopensource.jobtracker.cmd;

import com.github.ltsopensource.core.cmd.HttpCmdNames;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

/**
 * Unit tests for {@link KillLTSTaskHttpCmd}.
 */
public class KillLTSTaskHttpCmdTest {

    @Test
    public void getCommandTest() {
        JobTrackerAppContext jobTrackerAppContext = PowerMockito.mock(JobTrackerAppContext.class);
        KillLTSTaskHttpCmd cmd = new KillLTSTaskHttpCmd(jobTrackerAppContext);
        Assert.assertEquals(HttpCmdNames.HTTP_CMD_KILL_LTS_TASK, cmd.getCommand());
    }
}
