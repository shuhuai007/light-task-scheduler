package com.github.ltsopensource.core.domain;


import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.JobNodeType;
import com.github.ltsopensource.core.exception.JobSubmitException;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.core.support.CronExpression;
import com.github.ltsopensource.remoting.annotation.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robert HG (254963746@qq.com) on 8/13/14.
 */
public class Job implements Serializable {

    private static final long serialVersionUID = 7881199011994149340L;

    private String taskId;
    /**
     * 优先级 (数值越大 优先级越低)
     */
    private Integer priority = 100;
    // 提交的节点 （可以手动指定）
    private String submitNodeGroup;
    // 执行的节点
    @NotNull
    private String taskTrackerNodeGroup;

    private Map<String, String> extParams;
    // 是否要反馈给客户端
    private boolean needFeedback = false;
    // 该任务最大的重试次数
    private int maxRetryTimes = 0;

    /**
     * Retry internal of this job. (JDL: workflow->jobs->retryInterval)
     */
    private int retryInternal;
    /**
     * 执行表达式 和 quartz 的一样
     * 如果这个为空，表示立即执行的
     */
    private String cronExpression;

    /**
     * 重复次数 (-1 表示无限制重复)
     */
    private int repeatCount = 0;
    /**
     * 重复interval
     */
    private Long repeatInterval;
    /**
     * 任务的最触发发时间
     * 如果设置了 cronExpression， 那么这个字段没用
     */
    private Long triggerTime;
    /**
     * 当任务队列中存在这个任务的时候，是否替换更新
     */
    private boolean replaceOnExist = false;
    /**
     * 是否依赖上一个执行周期(对于周期性任务才起作用)
     */
    private boolean relyOnPrevCycle = true;
    /**
     * Workflow name. (JDL:taskName).
     */
    private String workflowName;
    /**
     * Workflow dependencies. (JDL:depends)
     */
    private List<String> workflowDepends;
    /**
     * The start time of workflow. (JDL: coordinator->start)
     */
    private Long startTime;
    /**
     * The end time of workflow. (JDL: coordinator->end)
     */
    private Long endTime;
    /**
     * Job node type.
     */
    private JobNodeType jobNodeType;
    /**
     * Workflow id which this job belongs to, static id. (Task table:id)
     */
    private String workflowId;
    /**
     * Job name of this job.
     */
    private String jobName;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSubmitNodeGroup() {
        return submitNodeGroup;
    }

    public void setSubmitNodeGroup(String submitNodeGroup) {
        this.submitNodeGroup = submitNodeGroup;
    }

    public String getTaskTrackerNodeGroup() {
        return taskTrackerNodeGroup;
    }

    public void setTaskTrackerNodeGroup(String taskTrackerNodeGroup) {
        this.taskTrackerNodeGroup = taskTrackerNodeGroup;
    }

    public boolean isNeedFeedback() {
        return needFeedback;
    }

    public void setNeedFeedback(boolean needFeedback) {
        this.needFeedback = needFeedback;
    }

    public Map<String, String> getExtParams() {
        return extParams;
    }

    public void setExtParams(Map<String, String> extParams) {
        this.extParams = extParams;
    }

    public String getParam(String key) {
        if (extParams == null) {
            return null;
        }
        return extParams.get(key);
    }

    public void setParam(String key, String value) {
        if (extParams == null) {
            extParams = new HashMap<String, String>();
        }
        extParams.put(key, value);
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isCron() {
        return this.cronExpression != null && !"".equals(this.cronExpression.trim());
    }

    public boolean isRepeatable() {
        return (this.repeatInterval != null && this.repeatInterval > 0) && (this.repeatCount >= -1 && this.repeatCount != 0);
    }

    public void setTriggerDate(Date date) {
        if (date != null) {
            this.triggerTime = date.getTime();
        }
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public boolean isReplaceOnExist() {
        return replaceOnExist;
    }

    public void setReplaceOnExist(boolean replaceOnExist) {
        this.replaceOnExist = replaceOnExist;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public boolean isRelyOnPrevCycle() {
        return relyOnPrevCycle;
    }

    public void setRelyOnPrevCycle(boolean relyOnPrevCycle) {
        this.relyOnPrevCycle = relyOnPrevCycle;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public void checkField() throws JobSubmitException {
        if (taskTrackerNodeGroup == null) {
            throw new JobSubmitException("taskTrackerNodeGroup can not be null! job is " + toString());
        }
        if (StringUtils.isNotEmpty(cronExpression) && !CronExpression.isValidExpression(cronExpression)) {
            throw new JobSubmitException("cronExpression invalid! job is " + toString());
        }
        if (maxRetryTimes < 0) {
            throw new JobSubmitException("maxRetryTimes invalid, must be great than zero! job is " + toString());
        }
        if (repeatCount < -1) {
            throw new JobSubmitException("repeatCount invalid, must be great than -1! job is " + toString());
        }
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public void setWorkflowDepends(List<String> workflowDependencyList) {
        this.workflowDepends = workflowDependencyList;
    }

    public List<String> getWorkflowDepends() {
        return workflowDepends;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public JobNodeType getJobNodeType() {
        return jobNodeType;
    }

    public void setJobNodeType(JobNodeType jobNodeType) {
        this.jobNodeType = jobNodeType;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getRetryInternal() {
        return retryInternal;
    }

    public void setRetryInternal(int retryInternal) {
        this.retryInternal = retryInternal;
    }
}
