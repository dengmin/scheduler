package com.rose.scheduler.core.component;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.Constants;
import com.rose.scheduler.core.TaskExecContext;
import com.rose.scheduler.core.TaskExecUtil;
import com.rose.scheduler.exception.RoseException;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellJobComponent extends JobComponent{
    private Logger logger = LoggerFactory.getLogger(ShellJobComponent.class);
    @Override
    public String getName() {
        return "ShellJobComponent";
    }

    @Override
    public String getDescription() {
        return "执行脚本";
    }

    @Override
    public String getParamTemplate() {
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("    shell:''\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public boolean run(TaskExecContext context) throws RoseException {
        JSONObject taskParam = context.getTaskParam();
        try {
            String shell = taskParam.getString("shell");
            String output = TaskExecUtil.execCommand(shell);
            context.log("output:");
            context.log(output);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw new RoseException(Constants.error_code_task_exec, e.getMessage());
        }
    }
}
