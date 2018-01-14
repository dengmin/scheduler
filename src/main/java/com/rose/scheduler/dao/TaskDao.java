package com.rose.scheduler.dao;

import com.rose.scheduler.entity.Pageable;
import com.rose.scheduler.entity.Task;
import com.rose.scheduler.entity.TaskHistory;

public interface TaskDao {
    public Pageable<Task> getTasks(String name, String group, String state, int page);
    public Pageable<TaskHistory> getHistory(String name, String group, Long starTimeFrom, Long startTimeTo, int page);
    TaskHistory detail(String fireId);
}
