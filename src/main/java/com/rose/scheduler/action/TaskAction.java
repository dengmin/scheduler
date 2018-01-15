package com.rose.scheduler.action;

import com.alibaba.fastjson.JSON;
import com.rose.scheduler.entity.*;
import com.rose.scheduler.exception.RoseException;
import com.rose.scheduler.manager.TaskManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskAction {
    private Logger logger = LoggerFactory.getLogger(TaskAction.class);

    private TaskManager taskManager;

    @Autowired
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @GetMapping("/task/groups")
    public Response taskGroups() throws Exception {
        return new Response(taskManager.getGroups());
    }

    @GetMapping("/task/list")
    public Response taskList(String state, String taskName, String taskGroup, Integer page) throws Exception {
        state = StringUtils.trimToNull(state);
        taskName = StringUtils.trimToNull(taskName);
        taskGroup = StringUtils.trimToNull(taskGroup);
        page = page == null ? 1 : page;
        Pageable<Task> result = taskManager.getTasks(taskName, taskGroup, state, page);
        return new Response(result);
    }

    @PostMapping("/task/create")
    public Response task(@RequestBody TaskWrapper taskInfo) throws Exception{
        taskInfo.setName(StringUtils.trimToEmpty(taskInfo.getName()));
        taskInfo.setGroup(StringUtils.trimToEmpty(taskInfo.getGroup()));
        taskManager.createTask(taskInfo);
        return new Response("ok");
    }

    @PostMapping("task/edit")
    public Response edit(@RequestBody TaskWrapper taskInfo) throws Exception{
        taskInfo.setName(StringUtils.trimToEmpty(taskInfo.getName()));
        taskInfo.setGroup(StringUtils.trimToEmpty(taskInfo.getGroup()));
        if (StringUtils.isNotEmpty(taskInfo.getParams())) {
            try {
                JSON.parseObject(taskInfo.getParams());
            } catch (Exception e) {
                throw new RoseException(RoseException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }
        taskManager.editTask(taskInfo);
        return new Response("ok");
    }

    @PostMapping("/task/delete")
    public Response deleteTask(String name,String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.delete(name, group);
        return new Response("ok");
    }

    @PostMapping("/task/pause")
    public Response pauseTask(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.pause(name, group);
        return new Response("ok");
    }

    @PostMapping("/task/execute")
    public Response startTask(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.start(name, group);
        return new Response("ok");
    }

    @PostMapping("/task/resume")
    public Response resumeTask(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.resume(name, group);
        return new Response("ok");
    }

    @GetMapping("/task/detail")
    public Response detail(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        TaskWrapper task = taskManager.get(name, group);
        return new Response(task);
    }

    @GetMapping("/task/history")
    public Response history(String name, String group, Long beginTime, Long endTime,Integer page){
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        page = page == null ? 1 : page;
        Pageable<TaskHistory> results = taskManager.getHistory(name, group,beginTime,endTime, page);
        return new Response(results);
    }

    @GetMapping("/task/history/detail")
    public Response historyDetail(String fireId){
        TaskHistory history = taskManager.detail(fireId);
        return new Response(history);
    }

}
