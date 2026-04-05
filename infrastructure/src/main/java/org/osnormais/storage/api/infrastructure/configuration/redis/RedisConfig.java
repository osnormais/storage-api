package org.osnormais.storage.api.infrastructure.configuration.redis;

import java.time.Duration;

import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.infrastructure.concurrency.redis.RedisConcurrencyTrackerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(name = "application.vendor.concurrency-tracker", havingValue = "redis")
public class RedisConfig {

    @Bean
    RedisTemplate<String, Integer> redisTemplate(final RedisConnectionFactory factory) {
        final RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
        return template;
    }

    @Bean
    ConcurrencyTracker.Port redisConcurrencyTracker(
            final RedisTemplate<String, Integer> redisTemplate,
            final @Value("${redis.concurrency.tracker.ttl-seconds}") Long ttlSeconds) {
        return new RedisConcurrencyTrackerPort(redisTemplate, Duration.ofSeconds(ttlSeconds));
    }

}
