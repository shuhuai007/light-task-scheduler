package com.github.ltsopensource.jobtracker;

import com.github.ltsopensource.core.support.JobUtils;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.jobtracker.support.VirtualJobResolver;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.MysqlPreLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * PreLoader to handle virtual node when loading.
 */
public class VirtualJobFilterMysqlPreLoader extends MysqlPreLoader {
    private JobTrackerAppContext jobTrackerAppContext;
    public VirtualJobFilterMysqlPreLoader(JobTrackerAppContext appContext) {
        super(appContext);
        jobTrackerAppContext = appContext;
    }

    @Override
    protected List<JobPo> load(String loadTaskTrackerNodeGroup, int loadSize) {
        List<JobPo> actualNodeList = new ArrayList<JobPo>();
        List<JobPo> jobPoList = super.load(loadTaskTrackerNodeGroup, loadSize);
        for (JobPo jobPo : jobPoList) {
            if (JobUtils.isVirtualNode(jobPo)) {
                jobTrackerAppContext.getExecutableJobQueue().remove(jobPo.getTaskTrackerNodeGroup(),
                        jobPo.getJobId(), jobPo.getTriggerTime());
                VirtualJobResolver.handleVirtualJobWhenFinish(jobPo, jobTrackerAppContext);
            } else {
                actualNodeList.add(jobPo);
            }
        }
        return actualNodeList;
    }
}
