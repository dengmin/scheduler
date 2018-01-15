package com.rose.scheduler.manager;

import com.rose.scheduler.entity.Pageable;
import com.rose.scheduler.entity.Task;
import com.rose.scheduler.entity.TaskHistory;
import com.rose.scheduler.entity.TaskWrapper;

import java.util.List;

public interface TaskManager {

    public List<String> getGroups() throws Exception;

    public void createTask(TaskWrapper taskInfo) throws Exception;

    public void editTask(TaskWrapper taskInfo) throws Exception;

    //立即执行
    public void start(String name, String group) throws Exception;

    public void pause(String name, String group);

    public void resume(String name, String group);

    public void delete(String name, String group);

    public Pageable<Task> getTasks(String name, String group, String state, int page);

    public TaskWrapper get(String name, String group) throws Exception;

    Pageable<TaskHistory> getHistory(String name, String group, Long starTimeFrom, Long startTimeTo, int page);

    TaskHistory detail(String fireId);

}
