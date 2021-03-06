package com.github.ltsopensource.core.cmd;

/**
 * @author Robert HG (254963746@qq.com) on 10/27/15.
 */
public interface HttpCmdNames {

    String HTTP_CMD_LOAD_JOB = "job_load_cmd";

    String HTTP_CMD_ADD_JOB = "job_add_cmd";

    String HTTP_CMD_ADD_M_DATA = "monitor_data_add_cmd";

    String HTTP_CMD_STATUS_CHECK = "status_check_cmd";

    String HTTP_CMD_JVM_INFO_GET = "jvm_info_get_cmd";

    String HTTP_CMD_JOB_TERMINATE = "job_terminate_cmd";

    String HTTP_CMD_SUBMIT_LTS_TASK = "lts_task_submit_cmd";

    String HTTP_CMD_KILL_LTS_TASK = "lts_task_kill_cmd";

    String HTTP_CMD_SUSPEND_LTS_TASK = "lts_task_suspend_cmd";

    String HTTP_CMD_RESUME_LTS_TASK = "lts_task_resume_cmd";

    String HTTP_CMD_RERUN_LTS_TASK = "lts_task_rerun_cmd";
}
