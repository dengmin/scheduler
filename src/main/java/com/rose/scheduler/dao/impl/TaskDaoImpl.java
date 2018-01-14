package com.rose.scheduler.dao.impl;

import com.rose.scheduler.common.Constants;
import com.rose.scheduler.dao.TaskDao;
import com.rose.scheduler.entity.Pageable;
import com.rose.scheduler.entity.Task;
import com.rose.scheduler.entity.TaskHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class TaskDaoImpl implements TaskDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Pageable<Task> getTasks(String name, String group, String state, int page) {
        List<Object> args = new ArrayList<>();
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM QRTZ_TRIGGERS t1 JOIN QRTZ_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlQueryResult = new StringBuilder("SELECT t1.SCHED_NAME 'sched_name', t1.TRIGGER_NAME 'name',t1.TRIGGER_GROUP 'group',t1.TRIGGER_TYPE 'triggerType',t2.JOB_CLASS_NAME 'jobComponent',t1.TRIGGER_STATE 'state',t1.PREV_FIRE_TIME 'prevFireTime',t1.NEXT_FIRE_TIME 'nextFireTime',t1.START_TIME 'startTime',t1.END_TIME 'endTime',t1.MISFIRE_INSTR 'misfireInstr',t1.DESCRIPTION 'description', t1.PRIORITY 'priority' FROM QRTZ_TRIGGERS t1 JOIN QRTZ_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");

        StringBuilder sqlWhere = new StringBuilder(" WHERE t1.SCHED_NAME = ?");
        args.add(Constants.SCHEDULER_NAME);
        if (name != null) {
            sqlWhere.append(" AND t1.TRIGGER_NAME LIKE ?");
            args.add("%" + name + "%");
        }
        if (group != null) {
            sqlWhere.append(" AND t1.TRIGGER_GROUP = ?");
            args.add(group);
        }
        if (state != null) {
            sqlWhere.append(" AND t1.TRIGGER_STATE = ?");
            args.add(state);
        }
        // 查询记录总数
        Integer resultTotal = jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
        // 查询记录
        sqlQueryResult.append(sqlWhere).append(" ORDER BY t1.NEXT_FIRE_TIME ASC LIMIT ?,?");
        args.add((page - 1) * Constants.pageSize);
        args.add(Constants.pageSize);

        final List<Task> result = new ArrayList<>();
        jdbcTemplate.query(sqlQueryResult.toString(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {

                Task task = new Task();
                task.setName(rs.getString("name"));
                task.setSchedulerName(rs.getString("sched_name"));
                task.setGroup(rs.getString("group"));
                task.setTriggerType(rs.getString("triggerType"));
                String jobComponent = rs.getString("jobComponent");
                task.setJobComponent(jobComponent.substring(jobComponent.lastIndexOf(".") + 1));
                task.setPrevFireTime(rs.getLong("prevFireTime"));
                task.setNextFireTime(rs.getLong("nextFireTime"));
                task.setStartTime(rs.getLong("startTime"));
                task.setEndTime(rs.getLong("endTime"));
                task.setMisfireInstr(rs.getInt("misfireInstr"));
                task.setState(rs.getString("state"));
                task.setPriority(rs.getInt("priority"));
                task.setDescription(rs.getString("description"));
                result.add(task);
            }
        }, args.toArray());

        return new Pageable<>(page, Constants.pageSize, resultTotal, result);
    }

    @Override
    public Pageable<TaskHistory> getHistory(String name, String group, Long starTimeFrom, Long startTimeTo, int page) {
        List<Object> args = new ArrayList<>();
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM TASK_HISTORY");
        StringBuilder sqlQueryResult = new StringBuilder("SELECT t.SCHED_NAME 'schedulerName',t.INSTANCE_ID 'instanceId',t.FIRE_ID 'fireId',t.TASK_NAME 'name',t.TASK_GROUP 'group',t.FIRED_TIME 'firedTime',t.FIRED_WAY 'firedWay',t.COMPLETE_TIME 'completeTime',t.EXPEND_TIME 'expendTime',t.REFIRED 'refired',t.EXEC_STATE 'execState' FROM TASK_HISTORY t");

        StringBuilder sqlWhere = new StringBuilder(" WHERE SCHED_NAME = ?");
        args.add(Constants.SCHEDULER_NAME);
        if (name != null) {
            sqlWhere.append(" AND TASK_NAME LIKE ?");
            args.add("%" + name + "%");
        }
        if (group != null) {
            sqlWhere.append(" AND TASK_GROUP LIKE ?");
            args.add("%" + group + "%");
        }
        if (starTimeFrom != null) {
            sqlWhere.append(" AND FIRED_TIME >= ?");
            args.add(starTimeFrom);
        }
        if (startTimeTo != null) {
            sqlWhere.append(" AND FIRED_TIME <= ?");
            args.add(startTimeTo);
        }
        // 查询记录总数
        Integer resultTotal = jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
        // 查询记录
        sqlQueryResult.append(sqlWhere).append(" ORDER BY FIRED_TIME DESC LIMIT ?,?");

        args.add((page - 1) * Constants.pageSize);
        args.add(Constants.pageSize);
        List<TaskHistory> result = jdbcTemplate.query(sqlQueryResult.toString(), new BeanPropertyRowMapper<>(TaskHistory.class), args.toArray());

        return new Pageable<>(page, Constants.pageSize, resultTotal, result);
    }

    @Override
    public TaskHistory detail(String fireId) {
        String sqlQueryResult = "SELECT t.SCHED_NAME 'schedulerName',t.INSTANCE_ID 'instanceId',t.FIRE_ID 'fireId',t.TASK_NAME 'name',t.TASK_GROUP 'group',t.FIRED_TIME 'firedTime',t.FIRED_WAY 'firedWay',t.COMPLETE_TIME 'completeTime',t.EXPEND_TIME 'expendTime',t.REFIRED 'refired',t.EXEC_STATE 'execState',t.LOG 'log' FROM TASK_HISTORY t WHERE t.FIRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQueryResult, new BeanPropertyRowMapper<>(TaskHistory.class), fireId);
    }
}
