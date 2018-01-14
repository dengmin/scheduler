package com.rose.scheduler.core.component;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.common.Constants;
import com.rose.scheduler.core.OkHttpUtil;
import com.rose.scheduler.core.TaskExecContext;
import com.rose.scheduler.exception.RoseException;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

public class HttpJobComponent extends JobComponent{

    @Override
    public String getName() {
        return "HttpJobComponent";
    }

    @Override
    public String getDescription() {
        return "HTTP请求任务";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    url:'',\r" +
                "    method:'get',\r" +
                "    params:{},\r" +
                "    timeout:5000\r" +
                "}";
    }

    @Override
    public boolean run(TaskExecContext context) throws RoseException {
        JSONObject taskParam = context.getTaskParam();
        String url = taskParam.getString("url");
        int timeout = taskParam.getIntValue("timeout");
        String method = StringUtils.upperCase(taskParam.getString("method"));
        JSONObject requestParams = taskParam.getJSONObject("params");
        try {
            Response response = OkHttpUtil.execute(method, url, timeout, requestParams);
            if(response.isSuccessful()){
                String result = response.body().string();
                context.log("output:");
                context.log(result);
            }else{
                context.log("response code: " + response.code());
                context.log("request error: ");
                context.log(response.body().string());
            }
        }catch (Exception e){
            e.printStackTrace();
            context.log(e.getMessage(), e);
            throw new RoseException(Constants.error_code_task_exec);
        }
        return true;
    }
}
