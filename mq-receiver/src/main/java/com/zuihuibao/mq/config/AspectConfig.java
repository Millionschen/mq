package com.zuihuibao.mq.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by millions on 2016/8/25.
 */

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.zuihuibao.mq.aspect")
public class AspectConfig {
}
