package com.rose.scheduler.common;

public interface Constants {
    public static final int error_code_unknown = 101;
    public static final int error_code_require_login = 102;
    public static final int error_code_invalid_params = 103;
    public static final int error_code_incomplete_params = 104;
    public static final int error_code_invalid_request = 105;
    public static final int error_code_task_exec = 106;

    public static final String SCHEDULER_NAME = "RoseScheduler";
    public static final String JOB_TASK_PARAM = "task_params";
    public static final String TASK_EXEC_RESULT = "task_exec_result";

    public static final int pageSize = 20;
}
