package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.Constants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class TaskExecUtil implements Constants{
    public static JSONObject getTaskParam(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (mergedJobDataMap.getString(JOB_TASK_PARAM) == null) {
            return new JSONObject();
        }
        return JSONObject.parseObject(mergedJobDataMap.getString(JOB_TASK_PARAM));
    }
}
