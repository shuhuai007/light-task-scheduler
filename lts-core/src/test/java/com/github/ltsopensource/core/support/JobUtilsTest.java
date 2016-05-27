package com.github.ltsopensource.core.support;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.domain.BizLog;
import com.github.ltsopensource.queue.domain.JobPo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link JobUtils}.
 */
public class JobUtilsTest {
    @Test
    public void getParentListTest() {
        JobPo jobPo = new JobPo();
        String parents = "100,200,300";
        jobPo.setExtParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY, parents);

        List<String> parentList = JobUtils.getParentList(jobPo);
        Assert.assertEquals(3, parentList.size());
    }

    @Test
    public void copyJobLogPo2BizLogTest() {
        JobLogPo jobLogPo = new JobLogPo();
        jobLogPo.setJobId("XXXXXXXXX");
        jobLogPo.setInternalExtParam("key1", "value1");
        BizLog bizLog = JobUtils.copyJobLogPo2BizLog(jobLogPo);
        Assert.assertNotNull(bizLog);
        Assert.assertEquals(jobLogPo.getJobId(), bizLog.getJobId());
        Assert.assertEquals("value1", jobLogPo.getInternalExtParam("key1"));
        Assert.assertEquals("value1", bizLog.getInternalExtParam("key1"));

    }

    @Test
    public void copyBizLog2JobLogPoTest() {
        BizLog bizLog = new BizLog();
        bizLog.setJobId("XXXXXXXXX");
        bizLog.setInternalExtParam("key1", "value1");

        JobLogPo jobLogPo = JobUtils.copyBizLog2JobLogPo(bizLog);
        Assert.assertNotNull(bizLog);
        Assert.assertEquals(bizLog.getJobId(), jobLogPo.getJobId());
        Assert.assertEquals("value1", jobLogPo.getInternalExtParam("key1"));
        Assert.assertEquals("value1", bizLog.getInternalExtParam("key1"));

    }
}
