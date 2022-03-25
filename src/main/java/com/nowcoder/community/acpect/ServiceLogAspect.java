package com.nowcoder.community.acpect;

import com.nowcoder.community.controller.advice.ExceptionAdvice;
//import com.sun.xml.internal.bind.v2.TODO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    // 前置通知
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 日志格式
        // 用户[1.2.3.4],在[xxx],访问了[com.nowcoder.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes==null){  // 由于Consumer中也点用了Service，所以请求属性为空，所以这里不记录日志
            // TODO
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s]，在[%s]，访问了[%s]。", ip, now, target));
    }
}
