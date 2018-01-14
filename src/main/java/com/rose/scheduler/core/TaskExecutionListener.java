package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.AppCtx;
import com.rose.scheduler.common.Constants;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Calendar;

public class TaskExecutionListener extends TaskListenerSupport{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getName() {
        return "TaskExecutionListener";
    }

    @Override
    public void taskExecutionVetoed(TaskExecContext context) {
        super.taskExecutionVetoed(context);
    }

    @Override
    public void taskWasExecuted(TaskExecContext context, JobExecutionException jobException) {
        JobExecutionContext jobExecutionContext = context.getJobExecutionContext();
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        Scheduler scheduler = jobExecutionContext.getScheduler();
        Date currentTime = Calendar.getInstance().getTime();

        Object result = jobExecutionContext.get(Constants.TASK_EXEC_RESULT);
        String state = null == jobException ? "SUCCESS" : "FAIL";
        final String stdout = null != result ? result.toString() : "";
        try {
            JdbcTemplate jdbcTemplate = AppCtx.getBean(JdbcTemplate.class);
            String schedulerName = scheduler.getSchedulerName();
            String instance_id = scheduler.getSchedulerInstanceId();
            String sql ="insert into task_history(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME,FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            jdbcTemplate.execute(sql, new PreparedStatementCallback<Object>() {
                @Override
                public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    ps.setString(1, schedulerName);
                    ps.setString(2, instance_id);
                    ps.setString(3,jobExecutionContext.getFireInstanceId());
                    ps.setString(4, jobDetail.getKey().getName());
                    ps.setString(5, jobDetail.getKey().getGroup());
                    ps.setLong(6, jobExecutionContext.getFireTime().getTime());
                    ps.setString(7, "");
                    ps.setLong(8, currentTime.getTime());
                    ps.setLong(9, jobExecutionContext.getJobRunTime());
                    ps.setInt(10, jobExecutionContext.getRefireCount());
                    ps.setString(11, state);
                    ps.setString(12, stdout);
                    return  ps.execute();
                }
            });
        }catch (SchedulerException e){
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }
}
