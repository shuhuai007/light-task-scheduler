package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.client.LTSClient;
import com.github.ltsopensource.client.LTSClientException;
import com.github.ltsopensource.client.jdl.JDLConstants;
import com.github.ltsopensource.client.jdl.JDLObject;
import com.github.ltsopensource.client.jdl.JobObject;
import com.github.ltsopensource.core.commons.utils.UTCDateUtils;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.LTSTask;
import com.github.ltsopensource.core.json.JSON;
import org.apache.commons.lang.StringUtils;

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
//            System.out.println(jdlObject.getEngine());
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
     * @return lts task object
     */
    public static LTSTask generateLTSTask(String jdl, String taskId) throws LTSClientException {
        LTSTask ltsTask = new LTSTask();
        JDLObject jdlObject = parse(jdl);
        try {
            // add start node
            ltsTask.add(generateStartJob(jdlObject, taskId));

            List<JobObject> jobObjectList = jdlObject.getWorkflow().getJobs();
            for(JobObject jobObject : jobObjectList) {
                Job job = new Job();
                job.setWorkflowName(jdlObject.getTaskName());
                ltsTask.add(job);
            }
        } catch (Exception e) {
            throw new LTSClientException(e.getMessage());
        }

        return ltsTask;
    }

    private static Job generateStartJob(JDLObject jdlObject, String taskId) throws Exception {
        Job job = new Job();
        job.setWorkflowName(jdlObject.getTaskName());
        job.setWorkflowDepends(jdlObject.getDepends());
        job.setCronExpression(jdlObject.getCoordinator().getFrequency());
        job.setStartTime(UTCDateUtils.getCalendar(jdlObject.getCoordinator().getStart())
                .getTimeInMillis());
        job.setEndTime(UTCDateUtils.getCalendar(jdlObject.getCoordinator().getEnd())
                .getTimeInMillis());
        job.setParam(JDLConstants.COORDINATOR_CONTROLS_TIMEOUT,
                String.valueOf(jdlObject.getCoordinator().getControls().getTimeout()));
        job.setParam(JDLConstants.COORDINATOR_CONTROLS_CONCURRENCY,
                String.valueOf(jdlObject.getCoordinator().getControls().getConcurrency()));
        job.setParam(JDLConstants.COORDINATOR_CONTROLS_EXECUTION,
                String.valueOf(jdlObject.getCoordinator().getControls().getExecution()));
        job.setParam(JDLConstants.COORDINATOR_CONTROLS_THROTTLE,
                String.valueOf(jdlObject.getCoordinator().getControls().getThrottle()));
        job.setParam(JobInfoConstants.JOB_CHILDREN_PARAM_KEY, StringUtils.join(jdlObject.getWorkflow()
                .getStart(), ","));
        return job;
    }

    public static JDLObject parse(String jdl) {
        JDLObject jdlObject = JSON.parse(jdl, JDLObject.class);
        return jdlObject;
    }
}
