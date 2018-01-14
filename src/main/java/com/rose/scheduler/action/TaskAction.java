package com.rose.scheduler.action;

import com.rose.scheduler.entity.*;
import com.rose.scheduler.manager.TaskManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TaskAction {
    private Logger logger = LoggerFactory.getLogger(TaskAction.class);

    private TaskManager taskManager;

    @Autowired
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @ResponseBody
    @GetMapping("/task/groups")
    public Response taskGroups() throws Exception {
        return new Response(taskManager.getGroups());
    }

    @ResponseBody
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
    @ResponseBody
    public Response task(@RequestBody TaskWrapper taskInfo) throws Exception{
        taskInfo.setName(StringUtils.trimToEmpty(taskInfo.getName()));
        taskInfo.setGroup(StringUtils.trimToEmpty(taskInfo.getGroup()));
        taskManager.createTask(taskInfo);
        return new Response("ok");
    }

    @PostMapping("task/edit")
    @ResponseBody
    public Response edit(@RequestBody TaskWrapper taskInfo) throws Exception{
        return new Response("ok");
    }

    @PostMapping("/task/delete")
    @ResponseBody
    public Response delteTask(String name,String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.delete(name, group);
        return new Response("ok");
    }

    @PostMapping("/task/pause")
    @ResponseBody
    public Response pauseTask(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.pause(name, group);
        return new Response("ok");
    }

    @PostMapping("/task/execute")
    @ResponseBody
    public Response startTask(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.start(name, group);
        return new Response("ok");
    }

    @PostMapping("/task/resume")
    @ResponseBody
    public Response resumeTask(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        taskManager.resume(name, group);
        return new Response("ok");
    }

    @GetMapping("/task/detail")
    @ResponseBody
    public Response detail(String name, String group) throws Exception{
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        TaskWrapper task = taskManager.get(name, group);
        return new Response(task);
    }

    @GetMapping("/task/history")
    @ResponseBody
    public Response history(String name, String group, Long beginTime, Long endTime,Integer page){
        name = StringUtils.trimToNull(name);
        group = StringUtils.trimToNull(group);
        page = page == null ? 1 : page;
        Pageable<TaskHistory> results = taskManager.getHistory(name, group,beginTime,endTime, page);
        return new Response(results);
    }

    @GetMapping("/task/history/detail")
    @ResponseBody
    public Response historyDetail(String fireId){
        TaskHistory history = taskManager.detail(fireId);
        return new Response(history);
    }

}
