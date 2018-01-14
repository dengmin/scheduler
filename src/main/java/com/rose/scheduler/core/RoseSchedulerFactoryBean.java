package com.rose.scheduler.core;

import com.rose.scheduler.common.Constants;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RoseSchedulerFactoryBean  extends SchedulerFactoryBean {

    private List<TaskListenerSupport> taskListenerList = new ArrayList<>();

    public RoseSchedulerFactoryBean(DataSource dataSource){
        this.setSchedulerName(Constants.SCHEDULER_NAME);
        this.setDataSource(dataSource);
    }

    public void afterPropertiesSet() throws Exception {
        this.setQuartzProperties(quartzProperties());
        super.afterPropertiesSet();
    }

    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Override
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName) throws SchedulerException {
        Scheduler scheduler = super.createScheduler(schedulerFactory, schedulerName);
        taskListenerList.add(new TaskExecutionListener());

        for(TaskListenerSupport support: taskListenerList){
            scheduler.getListenerManager().addJobListener(support);
        }
        return scheduler;
    }

}
