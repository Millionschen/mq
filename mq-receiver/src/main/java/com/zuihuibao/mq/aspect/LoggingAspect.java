package com.zuihuibao.mq.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Component
public class LoggingAspect {

    @Before("execution(* com.zuihuibao.mq.dispatch.receiver.*(..))")
    public void logBefore() {
        System.out.println("before advice called");
    }

    @After("execution(* com.zuihuibao.mq.dispatch.receiver.*(..))")
    public void logAfter() {
        System.out.println("after advice called");
    }

}
