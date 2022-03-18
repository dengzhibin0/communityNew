package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/18 20:49
 * bean的初始化和销毁测试
 */
@Service
//@Scope("prototype")  // 作用范围，默认是单例，prototype多例
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;


//    public AlphaService(){
//        System.out.println("实例化AlphaService");
//    }
//
//    // PostConstruct构造器之后调用
//    @PostConstruct
//    public void init(){
//        System.out.println("初始化AlphaService");
//    }
//
//    // PreDestroy销毁之前调用
//    @PreDestroy
//    public void destory(){
//        System.out.println("销毁AlphaService");
//    }

    public String find(){
        return alphaDao.select();
    }

}
