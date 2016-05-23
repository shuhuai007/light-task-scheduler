package com.github.ltsopensource.core.domain;

import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobNodeType;
import com.github.ltsopensource.core.json.JSON;

import java.io.Serializable;
import java.util.*;

/**
 * LTS task definition.
 */
public class LTSTask implements Serializable  {
    private List<Job> dag = new ArrayList<Job>();

    public LTSTask add(Job job) {
        dag.add(job);
        return this;
    }

    public List<Job> getDag() {
        return dag;
    }

    public void setDag(List<Job> dag) {
        this.dag = dag;
    }

    public LTSTask addList(List<Job> jobList) {
        for (Job job : jobList) {
            add(job);
        }
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * Get start job.
     * @return start job
     */
    public Job getStart() {
        if (dag != null && !dag.isEmpty()) {
            return dag.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get end job.
     * @return end job
     */
    public Job getEnd() {
        if (dag != null && !dag.isEmpty()) {
            return dag.get(dag.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Reverse dependencies of dag, through setting the key JOB_PARAM_PARENTS_KEY of
     * {@link JobInfoConstants} for each job of dag.
     */
    public void reverseDependencies() {
        Map<String, HashSet<String>> cacheMap = new HashMap<String, HashSet<String>>();
        for (Job job : dag) {
            // end job has no child.
            if (job.getJobNodeType().equals(JobNodeType.END_JOB)) {
                continue;
            }
            String childrenStr = job.getParam(JobInfoConstants.JOB_PARAM_CHILDREN_KEY);
            for (String child : org.apache.commons.lang.StringUtils.split(childrenStr,
                    JobInfoConstants.JOB_PARENTS_CHILDREN_SEPARATOR)) {
                if (!cacheMap.containsKey(child)) {
                    cacheMap.put(child, new HashSet<String>());
                }
                HashSet<String> parentsList = cacheMap.get(child);
                parentsList.add(job.getJobName());
            }
        }

        for (Map.Entry<String, HashSet<String>> item : cacheMap.entrySet()) {
            String jobName = item.getKey();
            String parents = org.apache.commons.lang.StringUtils.join(item.getValue(),
                    JobInfoConstants.JOB_PARENTS_CHILDREN_SEPARATOR);
            Job job = search(dag, jobName);
            job.setParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY, parents);
        }
        this.getStart().setParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY, "");
    }

    /**
     * Find the {@link Job} based on job name.
     *
     * @param dag DAG represents the LTSTask
     * @param jobName job name to search
     * @return job info if exists
     */
    private Job search(List<Job> dag, String jobName) {

        for (Job job : dag) {
            if (job.getJobName().equals(jobName)) {
                return job;
            }
        }
        return null;
    }
}
