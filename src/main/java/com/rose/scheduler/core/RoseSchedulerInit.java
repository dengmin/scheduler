package com.rose.scheduler.core;

import com.rose.scheduler.common.AppCtx;
import com.rose.scheduler.core.component.HttpJobComponent;
import com.rose.scheduler.core.component.JobComponent;
import com.rose.scheduler.core.component.KettleJobComponent;
import com.rose.scheduler.core.component.ShellJobComponent;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoseSchedulerInit {
    private Logger logger = LoggerFactory.getLogger(RoseSchedulerInit.class);

    public void init() throws Exception{
        initJobComponent();

        startScheduler();
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
