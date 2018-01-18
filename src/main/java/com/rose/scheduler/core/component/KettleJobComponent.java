package com.rose.scheduler.core.component;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.AppCtx;
import com.rose.scheduler.config.KettleConfig;
import com.rose.scheduler.core.TaskExecContext;
import com.rose.scheduler.core.TaskExecUtil;
import com.rose.scheduler.exception.RoseException;

import java.io.File;

public class KettleJobComponent extends JobComponent{

    @Override
    public String getName() {
        return "KettleJobComponent";
    }

    @Override
    public String getDescription() {
        return "执行kettle任务";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "\tfile:''\r" +
                "}";
    }

    @Override
    public boolean run(TaskExecContext context) throws RoseException {
        JSONObject taskParam = context.getTaskParam();
        KettleConfig config = AppCtx.getBean(KettleConfig.class);

        String kettleWork = config.getWork();
        try {
            String file = taskParam.getString("file");
            String process = config.getHome() +File.separator+"kitchen.sh";
            String command = process + " /file " + kettleWork + File.separator + file +" /level Basic";
            System.out.println(command);
            String output = TaskExecUtil.execCommand(command);
            context.log(output);
        }catch (Exception e){
            context.log(e.getMessage(), e);
        }
        return false;
    }
}
