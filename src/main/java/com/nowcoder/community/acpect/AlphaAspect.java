package com.nowcoder.community.acpect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }

    // 前置通知
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        System.out.println("before");
    }
}
