package org.osnormais.storage.api.infrastructure.concurrency.redis;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.Identifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisConcurrencyTrackerPort implements ConcurrencyTracker.Port {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final RedisScript<Boolean> tryIncrementScript;
    private final RedisScript<Integer> incrementScript;
    private final RedisScript<Integer> decrementPositiveScript;
    private final ValueOperations<String, Integer> valueOperations;
    private final String ttl;

    public RedisConcurrencyTrackerPort(
            final RedisTemplate<String, Integer> redisTemplate,
            final Duration ttl) {
        this.redisTemplate = requireNonNull(redisTemplate);
        this.valueOperations = this.redisTemplate.opsForValue();
        this.ttl = String.valueOf(Math.max(1, requireNonNull(ttl).getSeconds()));
        this.tryIncrementScript = tryIncrementScript();
        this.incrementScript = incrementScript();
        this.decrementPositiveScript = decrementPositiveScript();
    }

    @Override
    public Boolean tryIncrement(Identifier<?> key, int maxConcurrent, String... tags) {
        return Boolean.TRUE.equals(redisTemplate.execute(
                tryIncrementScript,
                Collections.singletonList(buildKey(key, tags)),
                String.valueOf(maxConcurrent),
                ttl));
    }

    @Override
    public void increment(Identifier<?> key, String... tags) {

        redisTemplate.execute(
                incrementScript,
                Collections.singletonList(buildKey(key, tags)),
                ttl);

    }

    @Override
    public void decrement(Identifier<?> key, String... tags) {
        redisTemplate.execute(
                decrementPositiveScript,
                Collections.singletonList(buildKey(key, tags)));
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

    private static RedisScript<Boolean> tryIncrementScript() {
        return new DefaultRedisScript<>("""
                local current = tonumber(redis.call('get', KEYS[1])) or 0
                local max = tonumber(ARGV[1])
                if current < max then
                    redis.call('incr', KEYS[1])
                    redis.call('expire', KEYS[1], ARGV[2])
                    return 1
                end
                return 0
                """, Boolean.class);
    }

    private static RedisScript<Integer> decrementPositiveScript() {

        final String script = """
                local val = redis.call('decr', KEYS[1])

                if val <= 0 then
                    redis.call('del', KEYS[1])
                    return 0
                end

                return val
                """;

        return new DefaultRedisScript<>(script, Integer.class);
    }

    private static RedisScript<Integer> incrementScript() {

        final String script = """
                local val = redis.call('incr', KEYS[1])
                redis.call('expire', KEYS[1], ARGV[1])
                return val
                """;

        return new DefaultRedisScript<>(script, Integer.class);
    }

}
