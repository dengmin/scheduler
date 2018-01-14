package com.rose.scheduler;

import com.rose.scheduler.core.RoseSchedulerFactoryBean;
import com.rose.scheduler.core.RoseSchedulerInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import org.springframework.context.event.ContextRefreshedEvent;

import javax.sql.DataSource;

@SpringBootApplication
public class SchedulerApplication {

    private Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Bean
    public RoseSchedulerFactoryBean roseScheduler(DataSource dataSource){
        return new RoseSchedulerFactoryBean(dataSource);
    }

    /**
     * 系统启动后执行的监听器
     * @return
     */
    @Bean
    public ApplicationListener<ContextRefreshedEvent> applicationListener() {
        return new ApplicationListener<ContextRefreshedEvent>() {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
                ApplicationContext context = contextRefreshedEvent.getApplicationContext();
                logger.info("初始化系统监听器!");
                try {
                    new RoseSchedulerInit().init();
                }catch (Exception e){
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

}
