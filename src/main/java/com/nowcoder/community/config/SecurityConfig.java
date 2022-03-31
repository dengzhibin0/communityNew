package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");  // 忽略静态资源
    }

    // 授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();  // 禁用csrf
        // 授权相关的配置
        // accessDeniedPage：没权限的时候访问什么页面
        // anyRequest().permitAll():其他请求都可以访问
        http.authorizeRequests()
                .antMatchers("/user/setting",
                        "/user/upload",
                        "/user/updatePassword",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow")
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll();

        // accessDeniedHandler:权限不够时的处理
        // authenticationEntryPoint:没有登录时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        // 判断是不是异步请求(固定技巧)
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {  // 表示是异步请求
                            response.setContentType("application/plain;charset=utf-8");  // 返回一个字符串
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "请登录！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        // 判断是不是异步请求(固定技巧)
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {  // 表示是异步请求
                            response.setContentType("application/plain;charset=utf-8");  // 返回一个字符串
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "权限不足！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层会默认拦截logout请求，并处理
        // 这里覆盖它默认的逻辑，才能执行自定义的退出代码
        // /securitylogout这个路径只是欺骗，为了执行自定义的退出代码
        http.logout()
                .logoutUrl("/securitylogout");

    }
}
