package com.github.ltsopensource.biz.logger;

import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.JobLoggerRequest;
import com.github.ltsopensource.core.AppContext;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.spi.ServiceLoader;

import java.util.List;

/**
 * 内部根据用户参数决定是否采用延迟批量刷盘的策略,来提高吞吐量
 *
 * @author Robert HG (254963746@qq.com) on 10/2/15.
 */
public class SmartJobLogger implements JobLogger {

    private JobLogger delegate;

    public SmartJobLogger(AppContext appContext) {
        Config config = appContext.getConfig();
        JobLoggerFactory jobLoggerFactory = ServiceLoader.load(JobLoggerFactory.class, config);
        JobLogger jobLogger = jobLoggerFactory.getJobLogger(config);
        if (config.getParameter(ExtConfig.LAZY_JOB_LOGGER, false)) {
            this.delegate = new LazyJobLogger(appContext, jobLogger);
        } else {
            this.delegate = jobLogger;
        }
    }

    @Override
    public void log(JobLogPo jobLogPo) {
        this.delegate.log(jobLogPo);
    }

    @Override
    public void log(List<JobLogPo> jobLogPos) {
        this.delegate.log(jobLogPos);
    }

    @Override
    public PaginationRsp<JobLogPo> search(JobLoggerRequest request) {
        return this.delegate.search(request);
    }

    @Override
    public JobLogPo search(String workflowId, String taskId) {
        // TODO(zj): need to implement)
        return delegate.search(workflowId, taskId);
    }

    @Override
    public JobLogPo search(String workflowStaticId, String submitInstanceId,
                           Long triggerTime, String taskId) {
        // TODO(zj): need to implement)
        return delegate.search(workflowStaticId, submitInstanceId, triggerTime, taskId);
    }

    @Override
    public JobLogPo getJobLogPo(String workflowId, Long submitTime, String jobName,
                                Long triggerTime) {
        // TODO(zj): need to implement)
        return delegate.getJobLogPo(workflowId, submitTime, jobName, triggerTime);
    }

    @Override
    public List<JobLogPo> getJobLogPoListWithEndStatus(String workflowId, Long submitTime, Long triggerTime) {
        // TODO(zj): need to implement)
        return delegate.getJobLogPoListWithEndStatus(workflowId, submitTime, triggerTime);
    }

    @Override
    public Long getMaxSubmitTime(String workflowId, Long triggerTime) {
        // TODO(zj): need to implement)
        return delegate.getMaxSubmitTime(workflowId, triggerTime);
    }

    @Override
    public boolean remove(JobLogPo jobLogPo) {
        // TODO(zj): need to implement)
        return false;
    }
}
