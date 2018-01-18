package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.Constants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TaskExecUtil implements Constants{
    public static JSONObject getTaskParam(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (mergedJobDataMap.getString(JOB_TASK_PARAM) == null) {
            return new JSONObject();
        }
        return JSONObject.parseObject(mergedJobDataMap.getString(JOB_TASK_PARAM));
    }


    public static String execCommand(String command) throws Exception{
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        InputStream stderr = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = br.readLine()) != null) {
            output.append(line).append("\r");
        }
        br.close();
        isr.close();
        stderr.close();
        return output.toString();
    }
}
