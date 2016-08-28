package com.zuihuibao.mq.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan(basePackages =
        {
            "com.zuihuibao.mq.service",
            "com.zuihuibao.mq.task",
            "com.zuihuibao.mq.util"
        })
public class SpringConfig {
    private @Value("${spring-beans.corePoolSize}") int corePoolSize;

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(corePoolSize);
    }
}
