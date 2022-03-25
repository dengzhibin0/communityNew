package com.nowcoder.community.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/25 13:58
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void fireEvent(Event event){
        // 将事件发布出去
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
