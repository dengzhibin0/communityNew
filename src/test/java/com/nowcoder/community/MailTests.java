package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/20 13:36
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("1830601430@qq.com","test","这是一个测试邮件");
    }

    @Test
    public void testHtmlMail(){

        // 调用模板引擎生成动态网页
        Context context=new Context();
        context.setVariable("username","sunday");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("1830601430@qq.com","HTML",content);
    }
}
