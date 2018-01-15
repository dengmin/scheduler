package com.rose.scheduler.manager.impl;

import com.rose.scheduler.common.Constants;
import com.rose.scheduler.core.JobStore;
import com.rose.scheduler.core.component.JobComponent;
import com.rose.scheduler.dao.TaskDao;
import com.rose.scheduler.entity.Pageable;
import com.rose.scheduler.entity.Task;
import com.rose.scheduler.entity.TaskHistory;
import com.rose.scheduler.entity.TaskWrapper;
import com.rose.scheduler.exception.RoseException;
import com.rose.scheduler.manager.TaskManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class TaskManagerImpl implements TaskManager{

    private Scheduler scheduler;
    private TaskDao taskDao;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Autowired
    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public List<String> getGroups() throws Exception{
        return scheduler.getTriggerGroupNames();
    }

    @Override
    public void createTask(TaskWrapper taskInfo) throws Exception{
        //任务执行的组件
        Class<? extends JobComponent> jobComponentClass = JobStore.jobs.get(taskInfo.getJobComponent()).getClass();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.JOB_TASK_PARAM, taskInfo.getParams());

        JobDetail jobDetail = JobBuilder.newJob(jobComponentClass)
                .withIdentity(taskInfo.getName(), taskInfo.getGroup()).build();

        TriggerBuilder<Trigger> triggerBuilder = newTrigger()
                .withIdentity(taskInfo.getName(), taskInfo.getGroup())
                .usingJobData(dataMap).withDescription(taskInfo.getDescription());

        if (taskInfo.getStartAtType() == 1) {
            triggerBuilder.startNow();
        } else {
            triggerBuilder.startAt(taskInfo.getStartAt());
        }
        if (taskInfo.getEndAtType() != 1) {
            triggerBuilder.endAt(taskInfo.getEndAt());
        }
        if(taskInfo.getScheduleType() == 1){
            SimpleScheduleBuilder simpleScheduleBuilder = buildSimpleScheduler(taskInfo.getScheduleTypeSimpleOptions());
            SimpleTrigger trigger = triggerBuilder.withSchedule(simpleScheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }else if(taskInfo.getScheduleType() == 2){
            CalendarIntervalScheduleBuilder builder = buildCalendarScheduler(taskInfo.getScheduleTypeCalendarIntervalOptions());
            CalendarIntervalTrigger trigger = triggerBuilder.withSchedule(builder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }else if(taskInfo.getScheduleType() == 3){
            DailyTimeIntervalScheduleBuilder builder = buildDailyTimeScheduler(taskInfo.getScheduleTypeDailyTimeIntervalOptions());
            DailyTimeIntervalTrigger trigger = triggerBuilder.withSchedule(builder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }else if(taskInfo.getScheduleType() == 4){
            TaskWrapper.ScheduleTypeCronOptions options = taskInfo.getScheduleTypeCronOptions();
            if(!CronExpression.isValidExpression(options.getCron())){
                throw new RoseException(Constants.error_code_invalid_params, "Cron表达式有误");
            }
            CronScheduleBuilder builder = buildCronSchedule(options);
            CronTrigger trigger = triggerBuilder.withSchedule(builder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    @Override
    public void start(String name, String group) throws Exception{
        JobKey jobKey = new JobKey(name, group);
        Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
        JobDataMap jobDataMap = trigger.getJobDataMap();
        String randomTriggerName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
        OperableTrigger operableTrigger = (OperableTrigger) newTrigger().withIdentity(randomTriggerName, "test").forJob(jobKey).withDescription("手动执行【" + group + "." + name + "】").build();
        if (jobDataMap != null) {
            operableTrigger.setJobDataMap(jobDataMap);
        }
        scheduler.scheduleJob(operableTrigger);
    }

    @Override
    public void pause(String name, String group) {
        try {
            scheduler.pauseTrigger(new TriggerKey(name, group));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume(String name, String group) {
        try {
            scheduler.resumeTrigger(new TriggerKey(name, group));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name, String group) {
        try {
            scheduler.unscheduleJob(new TriggerKey(name, group));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private SimpleScheduleBuilder buildSimpleScheduler(TaskWrapper.ScheduleTypeSimpleOptions options){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        scheduleBuilder.withIntervalInMilliseconds(options.getInterval())
                .withRepeatCount(options.getRepeatType() == 1 ? -1 : options.getRepeatCount());
        if (options.getMisfireHandlingType() == -1) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (options.getMisfireHandlingType() == 1) {
            scheduleBuilder.withMisfireHandlingInstructionFireNow();
        } else if (options.getMisfireHandlingType() == 2) {
            scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
        } else if (options.getMisfireHandlingType() == 3) {
            scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
        } else if (options.getMisfireHandlingType() == 4) {
            scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
        } else if (options.getMisfireHandlingType() == 5) {
            scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
        }
        return scheduleBuilder;
    }

    private CalendarIntervalScheduleBuilder buildCalendarScheduler(TaskWrapper.ScheduleTypeCalendarIntervalOptions options){
        CalendarIntervalScheduleBuilder scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
        scheduleBuilder.withInterval(options.getInterval(), options.getIntervalUnit());

        if (options.getMisfireHandlingType() == -1) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (options.getMisfireHandlingType() == 1) {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else if (options.getMisfireHandlingType() == 2) {
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
        return scheduleBuilder;
    }

    private DailyTimeIntervalScheduleBuilder buildDailyTimeScheduler(TaskWrapper.ScheduleTypeDailyTimeIntervalOptions options){
        DailyTimeIntervalScheduleBuilder scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
        scheduleBuilder.withInterval(options.getInterval(), options.getIntervalUnit());

        if (options.getStartTimeOfDay() != null) {
            scheduleBuilder.startingDailyAt(options.getStartTimeOfDay());
        }
        if (options.getEndTimeOfDay() != null) {
            scheduleBuilder.endingDailyAt(options.getEndTimeOfDay());
        }
        if (ArrayUtils.isNotEmpty(options.getDaysOfWeek())) {
            scheduleBuilder.onDaysOfTheWeek(options.getDaysOfWeek());
        }
        if (options.getMisfireHandlingType() == -1) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (options.getMisfireHandlingType() == 1) {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else if (options.getMisfireHandlingType() == 2) {
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
        return scheduleBuilder;
    }

    private CronScheduleBuilder buildCronSchedule(TaskWrapper.ScheduleTypeCronOptions options){
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(options.getCron());
        if (options.getMisfireHandlingType() == -1) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (options.getMisfireHandlingType() == 1) {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else if (options.getMisfireHandlingType() == 2) {
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
        return scheduleBuilder;
    }

    public Pageable<Task> getTasks(String name, String group, String state, int page){
        return taskDao.getTasks(name, group, state, page);
    }

    @Override
    public TaskWrapper get(String name, String group) throws Exception{
        Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
        if(trigger == null) return null;

        JobKey jobKey = trigger.getJobKey();
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setName(jobKey.getName());
        wrapper.setGroup(jobKey.getGroup());
        wrapper.setStartAtType(trigger.getStartTime() == null ? 1 : 2);
        wrapper.setStartAt(trigger.getStartTime());
        wrapper.setEndAtType(trigger.getEndTime() == null ? 1 : 2);
        wrapper.setEndAt(trigger.getEndTime());
        wrapper.setJobComponent(jobDetail.getJobClass().getSimpleName());
        wrapper.setParams(trigger.getJobDataMap().getString(Constants.JOB_TASK_PARAM));
        wrapper.setDescription(trigger.getDescription());
        if (trigger instanceof SimpleTrigger) {
            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;

            wrapper.setScheduleType(1);

            TaskWrapper.ScheduleTypeSimpleOptions scheduleOptions = wrapper.getScheduleTypeSimpleOptions();
            scheduleOptions.setInterval(simpleTrigger.getRepeatInterval());
            scheduleOptions.setRepeatType(simpleTrigger.getRepeatCount() == -1 ? 1 : 2);
            scheduleOptions.setRepeatCount(simpleTrigger.getRepeatCount());
            scheduleOptions.setMisfireHandlingType(simpleTrigger.getMisfireInstruction());
        } else if (trigger instanceof CalendarIntervalTrigger) {
            CalendarIntervalTrigger calendarIntervalTrigger = (CalendarIntervalTrigger) trigger;

            wrapper.setScheduleType(2);

            TaskWrapper.ScheduleTypeCalendarIntervalOptions scheduleOptions = wrapper.getScheduleTypeCalendarIntervalOptions();
            scheduleOptions.setInterval(calendarIntervalTrigger.getRepeatInterval());
            scheduleOptions.setIntervalUnit(calendarIntervalTrigger.getRepeatIntervalUnit());
            scheduleOptions.setMisfireHandlingType(calendarIntervalTrigger.getMisfireInstruction());
        } else if (trigger instanceof DailyTimeIntervalTrigger) {
            DailyTimeIntervalTrigger dailyTimeIntervalTrigger = (DailyTimeIntervalTrigger) trigger;

            wrapper.setScheduleType(3);

            TaskWrapper.ScheduleTypeDailyTimeIntervalOptions scheduleOptions = wrapper.getScheduleTypeDailyTimeIntervalOptions();
            scheduleOptions.setStartTimeOfDay(dailyTimeIntervalTrigger.getStartTimeOfDay());
            scheduleOptions.setEndTimeOfDay(dailyTimeIntervalTrigger.getEndTimeOfDay());
            scheduleOptions.setDaysOfWeek(dailyTimeIntervalTrigger.getDaysOfWeek().toArray(new Integer[dailyTimeIntervalTrigger.getDaysOfWeek().size()]));
            scheduleOptions.setInterval(dailyTimeIntervalTrigger.getRepeatInterval());
            scheduleOptions.setIntervalUnit(dailyTimeIntervalTrigger.getRepeatIntervalUnit());
            scheduleOptions.setMisfireHandlingType(dailyTimeIntervalTrigger.getMisfireInstruction());
        } else if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;

            wrapper.setScheduleType(4);

            TaskWrapper.ScheduleTypeCronOptions scheduleOptions = wrapper.getScheduleTypeCronOptions();
            scheduleOptions.setCron(cronTrigger.getCronExpression());
            scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
        }
        return wrapper;
    }

    @Override
    public Pageable<TaskHistory> getHistory(String name, String group, Long starTimeFrom, Long startTimeTo, int page) {
        return taskDao.getHistory(name, group, starTimeFrom, startTimeTo, page);
    }

    @Override
    public TaskHistory detail(String fireId) {
        return taskDao.detail(fireId);
    }

    @Override
    public void editTask(TaskWrapper taskInfo) throws Exception{
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.JOB_TASK_PARAM, taskInfo.getParams());
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(taskInfo.getName(), taskInfo.getGroup())
                .usingJobData(dataMap).withDescription(taskInfo.getDescription());

        if (taskInfo.getStartAtType() == 1) {
            triggerBuilder.startNow();
        } else {
            triggerBuilder.startAt(taskInfo.getStartAt());
        }
        if (taskInfo.getEndAtType() != 1) {
            triggerBuilder.endAt(taskInfo.getEndAt());
        }
        if(taskInfo.getScheduleType() == 1){
            SimpleScheduleBuilder simpleScheduleBuilder = buildSimpleScheduler(taskInfo.getScheduleTypeSimpleOptions());
            SimpleTrigger trigger = triggerBuilder.withSchedule(simpleScheduleBuilder).build();
            scheduler.rescheduleJob(trigger.getKey(),trigger);
        }else if(taskInfo.getScheduleType() == 2){
            CalendarIntervalScheduleBuilder builder = buildCalendarScheduler(taskInfo.getScheduleTypeCalendarIntervalOptions());
            CalendarIntervalTrigger trigger = triggerBuilder.withSchedule(builder).build();
            scheduler.rescheduleJob(trigger.getKey(),trigger);
        }else if(taskInfo.getScheduleType() == 3){
            DailyTimeIntervalScheduleBuilder builder = buildDailyTimeScheduler(taskInfo.getScheduleTypeDailyTimeIntervalOptions());
            DailyTimeIntervalTrigger trigger = triggerBuilder.withSchedule(builder).build();
            scheduler.rescheduleJob(trigger.getKey(),trigger);
        }else if(taskInfo.getScheduleType() == 4){
            TaskWrapper.ScheduleTypeCronOptions options = taskInfo.getScheduleTypeCronOptions();
            if(!CronExpression.isValidExpression(options.getCron())){
                throw new RoseException(Constants.error_code_invalid_params, "Cron表达式有误");
            }
            CronScheduleBuilder builder = buildCronSchedule(options);
            CronTrigger trigger = triggerBuilder.withSchedule(builder).build();
            scheduler.rescheduleJob(trigger.getKey(),trigger);
        }
    }
}
