package com.tj.event.subscribe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
//@EnableScheduling
@Slf4j
public class TestSubPub {
    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    // @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        String s = String.valueOf(Math.random());
        log.info("发送消息:{}", s);
        redisTemplate.convertAndSend("test", s);
    }
}
