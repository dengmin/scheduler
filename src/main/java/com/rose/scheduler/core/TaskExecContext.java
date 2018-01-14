package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskExecContext {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private JobExecutionContext jobExecutionContext;
    private JSONObject taskParam;
    private StringBuilder logs = new StringBuilder();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public void setJobExecutionContext(JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;
    }

    public JSONObject getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(JSONObject taskParam) {
        this.taskParam = taskParam;
    }

    public void log(String msg){
        append(msg, null);
        logger.info(msg);
    }

    private void append(String msg, Throwable e){
        logs.append(sdf.format(new Date())).append(" - ").append(msg).append("\r\n");
    }

    public void log(String msg, Throwable e){
        log(msg);
        if (e != null) {
            log("StackTrace:");
            StackTraceElement[] stackArray = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackArray) {
                log(stackTraceElement.toString());
            }
            Throwable cause = e.getCause();
            if (cause != null) {
                log("Cause:" + cause.getMessage());
                for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
                    log(stackTraceElement.toString());
                }
            }
        }
    }

    public String getLogs(){
        return logs.toString();
    }
}
