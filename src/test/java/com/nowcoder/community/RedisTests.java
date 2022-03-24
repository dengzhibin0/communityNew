package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);  // 存
        System.out.println(redisTemplate.opsForValue().get(redisKey));  // 取
        System.out.println(redisTemplate.opsForValue().increment(redisKey));  // 增加
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));  // 删除
    }

    @Test
    public void testHash() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);  // 存
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");  // 存
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));  // 取
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));  // 取
    }

    @Test
    public void testList() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 101);  // 存
        redisTemplate.opsForList().leftPush(redisKey, 102);  // 存
        redisTemplate.opsForList().leftPush(redisKey, 103);  // 存
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));  // 取
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));  // 取
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey, "刘备", "张飞", "关羽");  // 存
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey, "刘备", 80);  // 存
        redisTemplate.opsForZSet().add(redisKey, "张飞", 90);  // 存
        redisTemplate.opsForZSet().add(redisKey, "关羽", 85);  // 存
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));  //　统计数据量
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "关羽"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "张飞"));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");  // 删除key
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
        System.out.println(redisTemplate.hasKey("test:students"));
    }

    // 多次访问同一个key，以绑定的形式访问
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        System.out.println(operations.get());
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 事务管理，编程式事务
    @Test
    public void testTransactional() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                operations.multi();  // 启动事务

                operations.opsForSet().add(redisKey, "张三");
                operations.opsForSet().add(redisKey, "李四");
                operations.opsForSet().add(redisKey, "王五");

                System.out.println(operations.opsForSet().members(redisKey));
                return operations.exec(); // 提交事务
            }
        });
        System.out.println(object);
    }
}
