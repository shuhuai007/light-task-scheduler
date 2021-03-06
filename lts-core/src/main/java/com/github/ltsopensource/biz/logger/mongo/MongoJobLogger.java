package com.github.ltsopensource.biz.logger.mongo;


import com.github.ltsopensource.biz.logger.JobLogger;
import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.JobLoggerRequest;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.store.mongo.MongoRepository;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.mongodb.morphia.query.Query;

import java.util.Date;
import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 3/27/15.
 */
public class MongoJobLogger extends MongoRepository implements JobLogger {

    public MongoJobLogger(Config config) {
        super(config);
        setTableName("lts_job_log_po");

        // create table
        DBCollection dbCollection = template.getCollection();
        List<DBObject> indexInfo = dbCollection.getIndexInfo();
        // create index if not exist
        if (CollectionUtils.sizeOf(indexInfo) <= 1) {
            template.ensureIndex("idx_logTime", "logTime");
            template.ensureIndex("idx_taskId_taskTrackerNodeGroup", "taskId,taskTrackerNodeGroup");
            template.ensureIndex("idx_realTaskId_taskTrackerNodeGroup", "realTaskId, taskTrackerNodeGroup");
        }
    }

    @Override
    public void log(JobLogPo jobLogPo) {
        template.save(jobLogPo);
    }

    @Override
    public void log(List<JobLogPo> jobLogPos) {
        template.save(jobLogPos);
    }

    @Override
    public PaginationRsp<JobLogPo> search(JobLoggerRequest request) {

        Query<JobLogPo> query = template.createQuery(JobLogPo.class);
        if(StringUtils.isNotEmpty(request.getTaskId())){
            query.field("taskId").equal(request.getTaskId());
        }
        if(StringUtils.isNotEmpty(request.getTaskTrackerNodeGroup())){
            query.field("taskTrackerNodeGroup").equal(request.getTaskTrackerNodeGroup());
        }
        if (request.getStartLogTime() != null) {
            query.filter("logTime >= ", getTimestamp(request.getStartLogTime()));
        }
        if (request.getEndLogTime() != null) {
            query.filter("logTime <= ", getTimestamp(request.getEndLogTime()));
        }
        PaginationRsp<JobLogPo> paginationRsp = new PaginationRsp<JobLogPo>();
        Long results = template.getCount(query);
        paginationRsp.setResults(results.intValue());
        if (results == 0) {
            return paginationRsp;
        }
        // 查询rows
        query.order("-logTime").offset(request.getStart()).limit(request.getLimit());

        paginationRsp.setRows(query.asList());

        return paginationRsp;
    }

    @Override
    public JobLogPo search(String workflowId, String taskId) {
        // TODO(zj): need to implement)
        return null;
    }

    @Override
    public JobLogPo search(String workflowStaticId, String submitInstanceId, Long triggerTime, String taskId) {
        // TODO(zj): need to implement)
        return null;
    }

    @Override
    public JobLogPo getJobLogPo(String workflowId, Long submitTime, String jobName, Long triggerTime) {
        // TODO(zj): need to implement)
        return null;
    }

    @Override
    public List<JobLogPo> getJobLogPoListWithEndStatus(String workflowId, Long submitTime, Long triggerTime) {
        // TODO(zj): need to implement)
        return null;
    }

    @Override
    public Long getMaxSubmitTime(String workflowId, Long triggerTime) {
        // TODO(zj): need to implement)
        return null;
    }

    @Override
    public boolean remove(JobLogPo jobLogPo) {
        // TODO(zj): need to implement)
        return false;
    }

    private Long getTimestamp(Date timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.getTime();
    }

}
