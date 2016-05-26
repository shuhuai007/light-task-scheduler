package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.client.LTSClientException;
import com.github.ltsopensource.client.jdl.ConfigurationObject;
import com.github.ltsopensource.client.jdl.ControlsObject;
import com.github.ltsopensource.client.jdl.JDLObject;
import com.github.ltsopensource.client.jdl.JobObject;
import com.github.ltsopensource.core.commons.utils.UTCDateUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobNodeType;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.LTSTask;
import com.github.ltsopensource.core.json.JSON;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parser of JDL.
 */
public class JDLParser {

    public static boolean verifyJDL(String jdl) {
        // TODO(zj): to be implemented
        try {
            JDLObject jdlObject = JSON.parse(jdl, JDLObject.class);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Parse the jdl, and generate the {@link LTSTask}.
     *
     * @param jdl jdl string
     * @param taskId generated from task database table
     * @param taskTrackerGroupName node group name
     * @return lts task object
     */
    public static LTSTask generateLTSTask(String jdl, String taskId, String taskTrackerGroupName) throws
            LTSClientException {
        LTSTask ltsTask = compositeLTSTask(jdl, taskId, taskTrackerGroupName);

        // Reverse ltsTask to add parent dependencies.
        ltsTask.reverseDependencies();
        // Set job type for all the jobs.
        ltsTask.updateJobType();
        // Repair trigger time for single period task, including realTime task and triggerTime task.
        ltsTask.updateTriggerTime();
        return ltsTask;
    }

    private static LTSTask compositeLTSTask(String jdl, String taskId, String taskTrackerGroupName)
            throws LTSClientException {
        LTSTask ltsTask = new LTSTask();
        JDLObject jdlObject = parse(jdl);
        Long submitTime = new Date().getTime();
        try {
            // Add start job.
            ltsTask.add(generateStartJob(jdlObject, taskId, taskTrackerGroupName, submitTime));
            // Add all the real jobs.
            List<JobObject> jobObjectList = jdlObject.getWorkflow().getJobs();
            for(JobObject jobObject : jobObjectList) {
                Job job = generateJob(jdlObject, taskId, taskTrackerGroupName, submitTime);
                if (jobObject.getType().equals(JobInfoConstants.WORKFLOW_JOBS_TYPE_SHELL)) {
                    job.setJobNodeType(JobNodeType.SHELL_JOB);
                }
                job.setJobName(jobObject.getName());
                job.setMaxRetryTimes(jobObject.getRetryMax());
                job.setRetryInternal(jobObject.getRetryInterval());
                job.setParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_PREPARE_KEY, jobObject.getPrepare());
                job.setParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_DECISION_KEY, StringUtils.join(jobObject
                        .getDecision(), JobInfoConstants.JOB_PARAM_DECISION_SEPARATOR));
                job.setParam(JobInfoConstants.JOB_PARAM_WORKFLOW_JOBS_CONFIGURATION_KEY,
                        transformConfiguration(jobObject.getConfiguration()));
                job.setParam(JobInfoConstants.JOB_PARAM_EXEC_KEY, jobObject.getExec());
                job.setParam(JobInfoConstants.JOB_PARAM_FILES_KEY, transformJobFiles(jobObject
                        .getFiles()));
                job.setParam(JobInfoConstants.JOB_PARAM_ARGUMENTS_KEY, transformArguments
                        (jobObject.getArguments()));
                job.setParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY, jobObject.getOK());
                ltsTask.add(job);
            }
            // Add end job.
            ltsTask.add(generateEndJob(jdlObject, taskId, taskTrackerGroupName, submitTime));

        } catch (Exception e) {
            throw new LTSClientException(e.getMessage());
        }
        return ltsTask;
    }

    private static String transformArguments(List<String> arguments) {
        return StringUtils.join(arguments, JobInfoConstants.JOB_ARGUMENTS_SEPARATOR);
    }

    private static String transformJobFiles(List<String> files) {
        return StringUtils.join(files, JobInfoConstants.JOB_FILES_SEPARATOR);
    }

    private static String transformConfiguration(List<ConfigurationObject> configuration) {
        List<String> configList = new ArrayList<String>();
        for(ConfigurationObject co : configuration) {
            String key = co.getName();
            String value = co.getValue();
            configList.add(key + JobInfoConstants.JOB_CONFIGURATION_KEY_VALUE_SEPARATOR + value);
        }
        return StringUtils.join(configList, JobInfoConstants.JOB_CONFIGURATION_ITEM_SEPARATOR);
    }

    private static Job generateStartJob(JDLObject jdlObject, String taskId,
            String taskTrackGroupName, Long submitTime) throws Exception {
        Job job = generateJob(jdlObject, taskId, taskTrackGroupName, submitTime);
        job.setJobName(JobInfoConstants.START_JOB_NAME);
        job.setJobNodeType(JobNodeType.START_JOB);
        job.setParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY, StringUtils.join(jdlObject.getWorkflow()
                .getStart(), ","));
        return job;
    }

    private static Job generateEndJob(JDLObject jdlObject, String taskId, String
            taskTrackGroupName, Long submitTime) throws
            Exception {
        Job job = generateJob(jdlObject, taskId, taskTrackGroupName, submitTime);
        job.setJobName(JobInfoConstants.END_JOB_NAME);
        job.setJobNodeType(JobNodeType.END_JOB);
        job.setParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY, "");
        return job;
    }

    private static Job generateJob(JDLObject jdlObject, String taskId, String taskTrackerGroupName, Long submitTime) throws Exception {
        Job job = new Job();
        job.setSubmitTime(submitTime);
        job.setTaskTrackerNodeGroup(taskTrackerGroupName);
        job.setWorkflowId(taskId);
        job.setWorkflowName(jdlObject.getTaskName());
        job.setWorkflowDepends(jdlObject.getDepends());
        job.setCronExpression(jdlObject.getCoordinator().getFrequency());
        if (jdlObject.getCoordinator().getStart() != null) {
            job.setStartTime(UTCDateUtils.getCalendar(jdlObject.getCoordinator().getStart())
                    .getTimeInMillis());
        }
        if (jdlObject.getCoordinator().getEnd() != null) {
            job.setEndTime(UTCDateUtils.getCalendar(jdlObject.getCoordinator().getEnd())
                    .getTimeInMillis());
        }
        job.setParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_TIMEOUT_KEY,
                String.valueOf(jdlObject.getCoordinator().getControls().getTimeout()));
        job.setParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_CONCURRENCY_KEY,
                String.valueOf(jdlObject.getCoordinator().getControls().getConcurrency()));
        job.setParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_EXECUTION_KEY,
                String.valueOf(jdlObject.getCoordinator().getControls().getExecution()));
        job.setParam(JobInfoConstants.JOB_PARAM_COORDINATOR_CONTROLS_THROTTLE_KEY,
                String.valueOf(jdlObject.getCoordinator().getControls().getThrottle()));

        setRelyOnPrevCycle(job, jdlObject.getCoordinator().getControls());
        return job;
    }

    private static void setRelyOnPrevCycle(Job job, ControlsObject controls) {
        if (controls.getConcurrency() == 1 && controls.getExecution().equals("FIFO")) {
            job.setRelyOnPrevCycle(true);
        } else {
            job.setRelyOnPrevCycle(false);
        }
    }

    public static JDLObject parse(String jdl) {
        JDLObject jdlObject = JSON.parse(jdl, JDLObject.class);
        return jdlObject;
    }
}
