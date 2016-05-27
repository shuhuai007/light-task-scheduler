package com.github.ltsopensource.queue.mysql;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.WaitingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.UpdateSql;
import com.github.ltsopensource.store.jdbc.exception.TableNotExistException;

import java.util.List;

/**
 * Mysql implementation.
 */
public class MysqlWaitingJobQueue extends AbstractMysqlJobQueue implements WaitingJobQueue {

    public MysqlWaitingJobQueue(Config config) {
        super(config);
        createTable(readSqlFile("sql/mysql/lts_waiting_job_queue.sql", getTableName()));

    }

    @Override
    protected String getTableName(JobQueueReq request) {
        return getTableName();
    }

    private String getTableName() {
        return JobQueueUtils.WAITING_JOB_QUEUE;
    }

    @Override
    public boolean add(JobPo jobPo) {
        try {
            return super.add(getTableName(), jobPo);
        } catch (TableNotExistException e) {
            // 表不存在
            createTable(readSqlFile("sql/mysql/lts_waiting_job_queue.sql", getTableName()));
            add(jobPo);
        }
        return true;
    }

    @Override
    public boolean remove(String taskTrackerNodeGroup, String jobId) {
        return new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .where("job_id = ?", jobId)
                .doDelete() == 1;
    }

    @Override
    public boolean removeBatch(String realTaskId, String taskTrackerNodeGroup) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .where("real_task_id = ?", realTaskId)
                .and("task_tracker_node_group = ?", taskTrackerNodeGroup)
                .doDelete();
        return true;
    }

    @Override
    public void resume(JobPo jobPo) {

        new UpdateSql(getSqlTemplate())
                .update()
                .table(getTableName())
                .set("is_running", false)
                .set("task_tracker_identity", null)
                .set("gmt_modified", SystemClock.now())
                .where("job_id=?", jobPo.getJobId())
                .doUpdate();
    }

    @Override
    public List<JobPo> getDeadJob(String taskTrackerNodeGroup, long deadline) {

        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("is_running = ?", true)
                .and("gmt_modified < ?", deadline)
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
    public List<JobPo> getAllJobs() {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    @Override
    public boolean remove(String workflowId, Long submitTime, String jobName, Long triggerTime) {
        return new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .where("workflow_id = ?", workflowId)
                .and("submit_time = ?", submitTime.longValue())
                .and("job_name = ?", jobName)
                .and("trigger_time = ?", triggerTime.longValue())
                .doDelete() == 1;
    }

    @Override
    public List<JobPo> getJobs(String jobId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("job_id = ?", jobId)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }
}
