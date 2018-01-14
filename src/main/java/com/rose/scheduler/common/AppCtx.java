package com.rose.scheduler.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppCtx implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private AppCtx() {
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppCtx.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

}
