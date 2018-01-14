package com.rose.scheduler.entity;

import java.io.Serializable;
import java.util.Date;

public class Response implements Serializable{
    private static final long serialVersionUID = -7447116450441232469L;
    private Integer code;
    private Object data;
    private String msg;
    private Date time = new Date();

    public Response(Object data, String msg) {
        this(0, data, msg);
    }

    public Response(Object data){
        this(0, data, "success");
    }

    public Response(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
