package org.osnormais.storage.api.infrastructure.concurrency.inmem;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.Identifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "application.vendor.concurrency-tracker", havingValue = "inmemory")
public class InMemoryConcurrencyTrackerPort implements ConcurrencyTracker.Port {

    private final ConcurrentMap<Key, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public void increment(final Identifier<?> key, final String... tags) {
        counters
                .computeIfAbsent(new Key(key, tags), k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    @Override
    public void decrement(final Identifier<?> key, final String... tags) {
        final Key mapKey = new Key(key, tags);

        counters.computeIfPresent(mapKey, (k, counter) -> {
            int value = counter.decrementAndGet();
            return value <= 0 ? null : counter;
        });
    }

    @Override
    public Integer getCurrentCount(final Identifier<?> key, final String... tags) {
        final AtomicInteger counter = counters.get(new Key(key, tags));
        return isNull(counter) ? 0 : counter.get();
    }

    private static final class Key {

        private final String identifierValue;
        private final String[] tags;

        private Key(final Identifier<?> identifier, final String... tags) {
            this.identifierValue = identifier.getStringValue();
            this.tags = isNull(tags) ? new String[0] : tags.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Key other))
                return false;
            return Objects.equals(identifierValue, other.identifierValue)
                    && Arrays.equals(tags, other.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifierValue, Arrays.hashCode(tags));
        }

    }
}