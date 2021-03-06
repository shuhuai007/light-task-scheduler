package com.github.ltsopensource.queue.mysql;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;

import java.util.List;

/**
 * Mysql implementation for {@link ExecutingJobQueue}.
 */
public class MysqlExecutingJobQueue extends AbstractMysqlJobQueue implements ExecutingJobQueue {

    public MysqlExecutingJobQueue(Config config) {
        super(config);
        // create table
        createTable(readSqlFile("sql/mysql/lts_executing_job_queue.sql", getTableName()));
    }

    @Override
    protected String getTableName(JobQueueReq request) {
        return getTableName();
    }

    @Override
    public boolean add(JobPo jobPo) {
        return super.add(getTableName(), jobPo);
    }

    @Override
    public boolean remove(String jobId) {
        return new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .where("job_id = ?", jobId)
                .doDelete() == 1;
    }

    @Override
    public List<JobPo> getJobs(String taskTrackerIdentity) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("task_tracker_identity = ?", taskTrackerIdentity)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    @Override
    public List<JobPo> getDeadJobs(long deadline) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("gmt_created < ?", deadline)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    @Override
    public JobPo getJob(String taskTrackerNodeGroup, String taskId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("task_id = ?", taskId)
                .and("task_tracker_node_group = ?", taskTrackerNodeGroup)
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    public JobPo getJob(String jobId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("job_id = ?", jobId)
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    public JobPo getJob(String workflowId, Long submitTime, String jobName, Long triggerTime) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("workflow_id = ?", workflowId)
                .and("submit_time = ?", submitTime.longValue())
                .and("job_name = ?", jobName)
                .and("trigger_time = ?", triggerTime.longValue())
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    public boolean removeBatchByWorkflowId(String workflowId) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .where("workflow_id = ?", workflowId)
                .doDelete();
        return true;
    }

    @Override
    public List<JobPo> getJobsByWorkflowId(String workflowId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("workflow_id = ?", workflowId)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    private String getTableName() {
        return JobQueueUtils.EXECUTING_JOB_QUEUE;
    }
}
