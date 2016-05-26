package com.github.ltsopensource.core.support;

import com.github.ltsopensource.core.constant.JobInfoConstants;
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
}
