package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/20 10:48
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {

    // 实例化
    private static final Logger logger= LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());  // 输出日志名字
        logger.debug("debug logger");  // 调试时使用
        logger.info("info logger");   //
        logger.warn("warn logger");
        logger.error("error logger");
    }
}
