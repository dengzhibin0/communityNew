package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/4/1 14:04
 */
@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 时间格式
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    // 将指定的ip计入UV
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    // 统计指定范围内的UV
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getUVKey(simpleDateFormat.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }

        // 合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(simpleDateFormat.format(start), simpleDateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 将指定用户计入DAV
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId,true);
    }

    // 统计指定范围内的DAU
    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(simpleDateFormat.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        // OR运算这些数据
        String redisKey = RedisKeyUtil.getDAUKey(simpleDateFormat.format(start), simpleDateFormat.format(end));
        return (long) redisTemplate.execute((RedisCallback) connection -> {
            connection.bitOp(RedisStringCommands.BitOperation.OR,
                    redisKey.getBytes(), keyList.toArray(new byte[0][0]));
            return connection.bitCount(redisKey.getBytes());
        });
    }
}
