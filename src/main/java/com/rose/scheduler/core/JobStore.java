package com.rose.scheduler.core;

import com.rose.scheduler.core.component.JobComponent;

import java.util.HashMap;
import java.util.Map;

public class JobStore {
    public static Map<String, JobComponent> jobs = new HashMap<String, JobComponent>();
}
