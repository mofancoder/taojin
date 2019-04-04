package com.tj.util.unique;

import com.tj.util.unique.service.IMachineFactory;
import com.tj.util.unique.service.impl.RedisMachineFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = false)
public class UniqueAutoConfiguration {


    @Bean
    public IMachineFactory redisMachineFactory(RedisTemplate redisTemplate) {
        RedisMachineFactory redisMachineFactory = new RedisMachineFactory();
        redisMachineFactory.setDebug(false);
        redisMachineFactory.setRedis(redisTemplate);
        redisMachineFactory.setRedisKey("MACHINE_IP_NS");
        redisMachineFactory.init();
        return redisMachineFactory;
    }


    @Bean
    public Unique unique(IMachineFactory redisMachineFactory) {
        Unique unique = new Unique(redisMachineFactory, 10, 12);
        return unique;
    }
}
