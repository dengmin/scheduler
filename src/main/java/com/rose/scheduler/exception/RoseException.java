package com.rose.scheduler.exception;

import com.rose.scheduler.common.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class RoseException extends RuntimeException implements Constants{
    private static final long serialVersionUID = -2080665057328636275L;

    private static HashMap<Integer, String> errorMsgMap = new HashMap<Integer, String>() {
        private static final long serialVersionUID = 4789780435570729443L;
        {
            put(error_code_unknown, "系统太忙，请稍后再试");
            put(error_code_require_login, "请先登录");
            put(error_code_invalid_params, "请求参数有误");
            put(error_code_incomplete_params, "缺失必选参数");
            put(error_code_invalid_request, "非法请求");
            put(error_code_task_exec, "任务执行错误");
        }
    };

    private int code;
    private Object data = new Object();

    public RoseException(Integer code) {
        this(code, errorMsgMap.get(code));
    }

    public RoseException(Integer code, Object data) {
        this(code, errorMsgMap.get(code), data);
    }

    public RoseException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public RoseException(Integer code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
