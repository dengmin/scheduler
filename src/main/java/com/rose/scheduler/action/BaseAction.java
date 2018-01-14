package com.rose.scheduler.action;

import com.alibaba.fastjson.JSONObject;
import com.rose.scheduler.core.JobStore;
import com.rose.scheduler.entity.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BaseAction {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/job/components")
    @ResponseBody
    public Response getComponents(){
        Map<String, Object> model = new HashMap<>();
        JSONObject jobComponents = new JSONObject();
        for (String jobComponentId : JobStore.jobs.keySet()) {
            jobComponents.put(jobComponentId, JobStore.jobs.get(jobComponentId));
        }
        return new Response(jobComponents);
    }

}
