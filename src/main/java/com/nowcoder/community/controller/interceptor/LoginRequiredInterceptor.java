package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {  // 判断拦截的目标是不是”HandlerMethod“类型
            HandlerMethod handlerMethod = (HandlerMethod) handler; // 转型
            Method method = handlerMethod.getMethod();   // 获取拦截的的方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);  // 获取这个注解
//            System.out.println(loginRequired);
//            System.out.println(hostHolder.getUser());
            if (loginRequired != null && hostHolder.getUser() == null) {  // 如果注解不等于空，但用户没有登录，那么就是非法请求
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
