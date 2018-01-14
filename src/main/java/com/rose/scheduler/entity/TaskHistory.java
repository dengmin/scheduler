package com.rose.scheduler.entity;

import java.io.Serializable;

/**
 * 任务执行历史
 */
public class TaskHistory implements Serializable{

    private static final long serialVersionUID = -9137670360037621077L;
    private String schedulerName;
    private String name;
    private String group;
    private String instanceId;
    private String fireId;
    private Long firedTime;
    private Long completeTime;
    private Long expendTime;
    private Integer refired;
    private String execState;
    private String log;

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getFireId() {
        return fireId;
    }

    public void setFireId(String fireId) {
        this.fireId = fireId;
    }

    public Long getFiredTime() {
        return firedTime;
    }

    public void setFiredTime(Long firedTime) {
        this.firedTime = firedTime;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }

    public Long getExpendTime() {
        return expendTime;
    }

    public void setExpendTime(Long expendTime) {
        this.expendTime = expendTime;
    }

    public Integer getRefired() {
        return refired;
    }

    public void setRefired(Integer refired) {
        this.refired = refired;
    }

    public String getExecState() {
        return execState;
    }

    public void setExecState(String execState) {
        this.execState = execState;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
