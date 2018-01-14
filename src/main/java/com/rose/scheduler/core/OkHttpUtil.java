package com.rose.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class OkHttpUtil {

    public static Response execute(Request request, long timeout) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        return client.newCall(request).execute();
    }

    public static Response get(String url, long timeout) throws IOException{
        Request request = new Request.Builder().url(url).build();
        return execute(request, timeout);
    }

    public static Response execute(String method, String url, long timeout, JSONObject params) throws IOException{
        if(StringUtils.equalsIgnoreCase(method, "POST")){
            //组装post的参数
            FormBody.Builder formBuild = new FormBody.Builder();
            if(null != params){
                Set<Map.Entry<String, Object>> entrys =  params.entrySet();
                for(Map.Entry entry: entrys){
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    formBuild.add(key.toString(), value.toString());
                }
            }
            // 生成post请求
            RequestBody body = formBuild.build();
            Request request = new Request.Builder().url(url).post(body).build();
            return execute(request, timeout);
        }else{
            if(null != params){
                Set<Map.Entry<String, Object>> entrys =  params.entrySet();
                StringBuilder sb = new StringBuilder();
                for(Map.Entry entry: entrys){
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    sb.append(key).append("=").append(value).append("&");
                }
                if(sb.length() > 0){
                    sb.deleteCharAt(sb.length() - 1);
                }
                if(url.contains("?")){
                    url = url + '&' + sb.toString();
                }else{
                    url = url + '?' + sb.toString();
                }
            }
            return get(url, timeout);
        }
    }
}
