package com.rose.scheduler.core.component;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.AppCtx;
import com.rose.scheduler.core.TaskExecContext;
import com.rose.scheduler.exception.RoseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

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
        JSONObject taskParam = context.getTaskParam();
        String task = taskParam.getString("task");
        if (StringUtils.equals("clear_task_history", task)) {
            int keepDays = taskParam.getInteger("keep_days");
            Calendar date_point = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
            date_point.set(Calendar.DAY_OF_MONTH, date_point.get(Calendar.DAY_OF_MONTH) - keepDays);
            JdbcTemplate jdbcTemplate = AppCtx.getBean(JdbcTemplate.class);
            String sql = "DELETE FROM TASK_HISTORY WHERE FIRED_TIME <= ?";
            jdbcTemplate.execute(sql, new PreparedStatementCallback<Object>() {
                @Override
                public Object doInPreparedStatement(PreparedStatement ps) throws DataAccessException {
                    try {
                        ps.setLong(1, date_point.getTimeInMillis());
                        int result = ps.executeUpdate();
                        context.log("清除任务执行历史， 已经清除 "+result+"条数据!");
                        return result;
                    } catch (SQLException e) {
                        context.log("任务执行失败", e);
                    }
                    return null;
                }
            });
        }
        return false;
    }
}
