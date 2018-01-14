package com.rose.scheduler.core.component;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.Constants;
import com.rose.scheduler.core.TaskExecContext;
import com.rose.scheduler.core.TaskExecUtil;
import com.rose.scheduler.exception.RoseException;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class JobComponent implements Job, Constants{

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getParamTemplate();

    public abstract boolean run(TaskExecContext context) throws RoseException;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JSONObject taskParams = TaskExecUtil.getTaskParam(context);
        TaskExecContext execContext = new TaskExecContext();
        execContext.setJobExecutionContext(context);
        execContext.setTaskParam(taskParams);
        execContext.log("开始执行任务 :" + jobDetail.getKey());
        execContext.log("任务参数: "+ taskParams.toJSONString());
        try {
            run(execContext);
        }catch (Exception e){
            execContext.log("任务执行失败", e);
            e.printStackTrace();
        }
        execContext.log("任务执行完毕!");
        context.put(Constants.TASK_EXEC_RESULT,execContext.getLogs());
    }
}
