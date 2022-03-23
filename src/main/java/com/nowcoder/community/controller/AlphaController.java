package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/18 20:15
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    // 模拟处理浏览器底层
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        // 遍历请求头
        Enumeration<String> enumeration=request.getHeaderNames();  // Enumeration：迭代器
        while (enumeration.hasMoreElements()){
            String name=enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+" "+value);
        }

        // 获取请求参数
        System.out.println(request.getParameter("code"));
        System.out.println(request.getParameter("name"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8"); // 设置返回的类型
        try (
                PrintWriter writer= response.getWriter();  // 放在这里会自动启动finally，关闭这个流
                ){
            writer.write("<h1>牛客网</h1>");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET请求
    // 例如分页查询
    // /students?current=1&limit=20
    // @ResponseBody的作用其实是将java对象转为json格式的数据。
    // @responseBody注解的作用是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，
    // 写入到response对象的body区，通常用来返回JSON数据或者是XML数据。
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /students/123
    @RequestMapping(path = "/student/{id}" ,method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    // POST请求
    @RequestMapping(path = "/student" ,method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应动态html数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView=new ModelAndView();  // 新建类

        // 添加数据
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age","26");

        // 数据加到哪个html，html可以省略
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    // 通过返回路径的方式，返回html
    @RequestMapping(path = "school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","清华大学");
        model.addAttribute("age","120");
        return "/demo/view";
    }

    // 响应JSON数据(异步请求中，异步请求就是网页不刷新的情况下访问服务器)
    // Java对象 -> JSON字符串 -> JS对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age","18");
        emp.put("salary","13000");
        return emp;
    }

    // 查询多个员工
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age","18");
        emp.put("salary","13000");
        list.add(emp);
        emp=new HashMap<>();
        emp.put("name","李四");
        emp.put("age","20");
        emp.put("salary","10000");
        list.add(emp);
        emp=new HashMap<>();
        emp.put("name","王五");
        emp.put("age","30");
        emp.put("salary","23000");
        list.add(emp);
        return list;
    }

    // cookie使用示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        // 创建cookie
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效的范围
        cookie.setPath("/community/alpha");
        // 设置生存时间
        cookie.setMaxAge(60*10);
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    // session使用示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    // session使用示例
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
//        System.out.println(name);
//        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功！");
    }
}
