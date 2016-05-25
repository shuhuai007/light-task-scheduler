package com.github.ltsopensource.biz.logger.mysql;

import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.biz.logger.JobLogger;
import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.JobLoggerRequest;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;
import com.github.ltsopensource.store.jdbc.builder.OrderByType;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;
import com.github.ltsopensource.store.jdbc.dbutils.JdbcTypeUtils;

import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 5/21/15.
 */
public class MysqlJobLogger extends JdbcAbstractAccess implements JobLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlJobLogger.class);

    public MysqlJobLogger(Config config) {
        super(config);
        createTable(readSqlFile("sql/mysql/lts_job_log_po.sql"));
    }

    @Override
    public void log(JobLogPo jobLogPo) {
        if (jobLogPo == null) {
            return;
        }
        InsertSql insertSql = buildInsertSql();

        setInsertSqlValues(insertSql, jobLogPo).doInsert();
    }

    @Override
    public void log(List<JobLogPo> jobLogPos) {
        if (CollectionUtils.isEmpty(jobLogPos)) {
            return;
        }

        InsertSql insertSql = buildInsertSql();

        for (JobLogPo jobLogPo : jobLogPos) {
            setInsertSqlValues(insertSql, jobLogPo);
        }
        insertSql.doBatchInsert();
    }

    private InsertSql buildInsertSql() {
        return new InsertSql(getSqlTemplate())
                .insert(getTableName())
                .columns("log_time",
                        "gmt_created",
                        "log_type",
                        "success",
                        "msg",
                        "task_tracker_identity",
                        "level",
                        "task_id",
                        "real_task_id",
                        "job_id",
                        "job_type",
                        "priority",
                        "submit_node_group",
                        "task_tracker_node_group",
                        "ext_params",
                        "internal_ext_params",
                        "need_feedback",
                        "cron_expression",
                        "trigger_time",
                        "retry_times",
                        "max_retry_times",
                        "rely_on_prev_cycle",
                        "repeat_count",
                        "repeated_count",
                        "repeat_interval"
                );
    }

    private InsertSql setInsertSqlValues(InsertSql insertSql, JobLogPo jobLogPo) {
        return insertSql.values(jobLogPo.getLogTime(),
                jobLogPo.getGmtCreated(),
                jobLogPo.getLogType().name(),
                jobLogPo.isSuccess(),
                jobLogPo.getMsg(),
                jobLogPo.getTaskTrackerIdentity(),
                jobLogPo.getLevel().name(),
                jobLogPo.getTaskId(),
                jobLogPo.getRealTaskId(),
                jobLogPo.getJobId(),
                jobLogPo.getJobType() == null ? null : jobLogPo.getJobType().name(),
                jobLogPo.getPriority(),
                jobLogPo.getSubmitNodeGroup(),
                jobLogPo.getTaskTrackerNodeGroup(),
                JSON.toJSONString(jobLogPo.getExtParams()),
                JSON.toJSONString(jobLogPo.getInternalExtParams()),
                jobLogPo.isNeedFeedback(),
                jobLogPo.getCronExpression(),
                jobLogPo.getTriggerTime(),
                jobLogPo.getRetryTimes(),
                jobLogPo.getMaxRetryTimes(),
                jobLogPo.getDepPreCycle(),
                jobLogPo.getRepeatCount(),
                jobLogPo.getRepeatedCount(),
                jobLogPo.getRepeatInterval());
    }

    @Override
    public PaginationRsp<JobLogPo> search(JobLoggerRequest request) {

        PaginationRsp<JobLogPo> response = new PaginationRsp<JobLogPo>();

        Long results = new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .single();
        response.setResults(results.intValue());
        if (results == 0) {
            return response;
        }
        // 查询 rows
        List<JobLogPo> rows = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .orderBy()
                .column("log_time", OrderByType.DESC)
                .limit(request.getStart(), request.getLimit())
                .list(RshHolder.JOB_LOGGER_LIST_RSH);
        response.setRows(rows);

        return response;
    }

    @Override
    public JobLogPo search(String workflowId, String taskId) {
        SelectSql sql = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("task_id = ?", taskId)
                .and(buildAndStatement(workflowId))
                .and("log_type = 'FINISHED'")
                .and("success = 1")
                .orderBy()
                .column("log_time", OrderByType.DESC);
        LOGGER.info("......search(workflowId, taskId): " + sql.getSQL());
        List<JobLogPo> rows = sql.list(RshHolder.JOB_LOGGER_LIST_RSH);
        if (rows != null && rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    @Override
    public JobLogPo search(String workflowStaticId, String submitInstanceId, Long triggerTime, String taskId) {
        SelectSql sql = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("task_id = ?", taskId)
                .and("trigger_time = ?", triggerTime)
                .and(buildAndStatement4ExtParams("workflowStaticId", workflowStaticId))
                .and(buildAndStatement4ExtParams("submitInstanceId", submitInstanceId))
                .and("log_type = 'FINISHED'")
                .and("success = 1")
                .orderBy()
                .column("log_time", OrderByType.DESC);
        LOGGER.info("......search(workflowStaticId,submitInstanceId, triggerTime, taskId): " +
                sql.getSQL());
        List<JobLogPo> rows = sql.list(RshHolder.JOB_LOGGER_LIST_RSH);
        if (rows != null && rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    @Override
    public JobLogPo getJobLogPo(String workflowId, Long submitTime, String jobName,
                                Long triggerTime) {
        // TODO(zj): need to refactor)
        SelectSql sql = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .where("workflow_id = ?", workflowId)
                .and("submit_time = ?", submitTime.longValue())
                .and("job_name = ?", jobName)
                .and("trigger_time = ?", triggerTime.longValue())
                .and("log_type = 'FINISHED'")
                .orderBy()
                .column("log_time", OrderByType.DESC);
        LOGGER.info("......getJobLogPo(workflowId, submitTime, jobName, triggerTime): " +
                sql.getSQL());
        List<JobLogPo> rows = sql.list(RshHolder.JOB_LOGGER_LIST_RSH);
        if (rows != null && rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    private String buildAndStatement4ExtParams(String key, String value) {
        // TODO(zj): need to implemented)
        String andStatement = "POSITION(" +
                "'\"" + key + "\":" +
                "\"" + value + "\"'" +
                " in ext_params)!=0";
        return andStatement;
    }

    private String buildAndStatement(String workflowId) {
        // TODO(zj): need to implemented)
        String andStatement = "POSITION(" +
                "'\"" + "wfInstanceId" + "\":" +
                "\"" + workflowId + "\"'" +
                " in ext_params)!=0";
        return andStatement;
    }

    private WhereSql buildWhereSql(JobLoggerRequest request) {
        return new WhereSql()
                .andOnNotEmpty("task_id = ?", request.getTaskId())
                .andOnNotEmpty("real_task_id = ?", request.getRealTaskId())
                .andOnNotEmpty("task_tracker_node_group = ?", request.getTaskTrackerNodeGroup())
                .andBetween("log_time", JdbcTypeUtils.toTimestamp(request.getStartLogTime()), JdbcTypeUtils.toTimestamp(request.getEndLogTime()))
                ;
    }

    private String getTableName() {
        return "lts_job_log_po";
    }
}
