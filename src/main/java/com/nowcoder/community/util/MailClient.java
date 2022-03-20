package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/20 11:31
 */
@Component
public class MailClient {
    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage messages=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(messages);
            helper.setFrom(from);  // 发送方
            helper.setTo(to);  // 接收放
            helper.setSubject(subject);   // 主题
            helper.setText(content,true); // true表示支持html文本
            mailSender.send(helper.getMimeMessage());
        }catch (MessagingException e){
            logger.error("发送邮件失败："+e.getMessage());
        }
    }

}
