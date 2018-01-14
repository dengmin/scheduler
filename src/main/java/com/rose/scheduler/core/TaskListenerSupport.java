package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public abstract  class TaskListenerSupport implements JobListener {

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        //任务 开始执行之前
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        //执行的任务被取消
        JSONObject taskParam = TaskExecUtil.getTaskParam(jobExecutionContext);

        TaskExecContext taskExecContext = new TaskExecContext();
        taskExecContext.setJobExecutionContext(jobExecutionContext);
        taskExecContext.setTaskParam(taskParam);

        taskExecutionVetoed(taskExecContext);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        //任务执行完成调用
        JSONObject taskParam = TaskExecUtil.getTaskParam(jobExecutionContext);

        TaskExecContext taskExecContext = new TaskExecContext();
        taskExecContext.setJobExecutionContext(jobExecutionContext);
        taskExecContext.setTaskParam(taskParam);
        taskWasExecuted(taskExecContext, e);
    }

    public void taskExecutionVetoed(TaskExecContext context) {
    }

    public void taskWasExecuted(TaskExecContext context, JobExecutionException jobException) {
    }
}
