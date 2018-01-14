package com.rose.scheduler.core.component;

import com.rose.scheduler.core.TaskExecContext;
import com.rose.scheduler.exception.RoseException;
import org.quartz.JobExecutionException;

public class BuildInJobComponent extends JobComponent{
    @Override
    public String getName() {
        return "BuildInJobComponent";
    }

    @Override
    public String getDescription() {
        return "执行系统内置任务";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    task:''\r" +
                "}";
    }

    @Override
    public boolean run(TaskExecContext context) throws RoseException {
        return false;
    }
}
