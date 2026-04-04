package org.osnormais.storage.api.infrastructure.concurrency.redis;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.Identifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisConcurrencyTrackerPort implements ConcurrencyTracker.Port {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final RedisScript<Integer> decrementPositiveScript;
    private final ValueOperations<String, Integer> valueOperations;
    private final Duration ttl;

    public RedisConcurrencyTrackerPort(
            final RedisTemplate<String, Integer> redisTemplate,
            final RedisScript<Integer> decrementPositiveScript,
            final Duration ttl) {
        this.redisTemplate = requireNonNull(redisTemplate);
        this.decrementPositiveScript = requireNonNull(decrementPositiveScript);
        this.valueOperations = this.redisTemplate.opsForValue();
        this.ttl = requireNonNull(ttl);
    }

    @Override
    public void increment(Identifier<?> key, String... tags) {
        valueOperations.increment(buildKey(key, tags));
        setTtl(buildKey(key, tags));
    }

    @Override
    public void decrement(Identifier<?> key, String... tags) {
        redisTemplate.execute(
                decrementPositiveScript,
                Collections.singletonList(buildKey(key, tags)));
        setTtl(buildKey(key, tags));
    }

    @Override
    public Integer getCurrentCount(Identifier<?> key, String... tags) {
        return Optional.ofNullable(valueOperations.get(buildKey(key, tags))).orElse(0);
    }

    private static String buildKey(Identifier<?> identifier, String... tags) {
        StringBuilder sb = new StringBuilder();
        sb.append(identifier.getStringValue());
        if (tags != null)
            for (String tag : tags)
                sb.append(":").append(tag);

        return sb.toString();
    }

    private void setTtl(final String key) {
        redisTemplate.expire(key, ttl);
    }

}
