package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.AppCtx;
import com.rose.scheduler.common.Constants;
import com.rose.scheduler.core.component.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoseSchedulerInit {
    private Logger logger = LoggerFactory.getLogger(RoseSchedulerInit.class);

    public void init() throws Exception{
        initJobComponent();

        startScheduler();
        initDefaultTask();
    }

    private void initJobComponent() throws Exception{
        logger.info("init job component..");
        JobComponent jobComponent = new HttpJobComponent();
        JobStore.jobs.put(jobComponent.getName(), jobComponent);
        jobComponent = new ShellJobComponent();
        JobStore.jobs.put(jobComponent.getName(), jobComponent);
        jobComponent = new KettleJobComponent();
        JobStore.jobs.put(jobComponent.getName(), jobComponent);
    }

    /**
     * 初始化默认的任务
     */
    private void initDefaultTask() throws SchedulerException{
        Scheduler scheduler = AppCtx.getBean(Scheduler.class);
        String name = "ClearTaskHistoryJob";
        String group = "BuildIn";
        String cron = "0 0 0 * * ?";
        String description = "内置任务，用于清除历史任务记录";
        if (!scheduler.checkExists(new JobKey(name, group))) {
            JobDetail jobDetail = JobBuilder.newJob(BuildInJobComponent.class).withIdentity(name, group).build();
            JobDataMap dataMap = new JobDataMap();
            JSONObject taskParam = new JSONObject();
            taskParam.put("task", "clear_task_history");
            taskParam.put("keep_days", 7);
            dataMap.put(Constants.JOB_TASK_PARAM, taskParam.toJSONString());

            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).usingJobData(dataMap).withDescription(description).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private void startScheduler(){
        Scheduler scheduler = AppCtx.getBean(Scheduler.class);
        try {
            scheduler.start();
            logger.info("启动scheduler");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
