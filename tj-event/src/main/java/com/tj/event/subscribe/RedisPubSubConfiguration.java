package com.tj.event.subscribe;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@ConditionalOnClass(value = {RedisTemplate.class, RedisConnection.class})
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = false)
public class RedisPubSubConfiguration {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, RedisMsg redisMsg) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(listenerAdapter(redisMsg), new PatternTopic("autodive"));
        return container;

    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisMsg redisMsg) {
        return new MessageListenerAdapter(redisMsg);
    }
}
