package org.osnormais.storage.api.application.port;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.domain.Identifier;

public class ConcurrencyTracker {

    private final ConcurrencyTracker.Port port;
    private final String[] tags;

    public ConcurrencyTracker(
            final ConcurrencyTracker.Port port,
            final String... tags) {
        this.port = requireNonNull(port);
        this.tags = isNull(tags) ? new String[0] : tags.clone();
    }

    public Boolean tryIncrement(Identifier<?> key, int maxConcurrent) {
        return port.tryIncrement(key, maxConcurrent, tags);
    }

    public void increment(final Identifier<?> key) {
        port.increment(key, tags);
    }

    public void decrement(final Identifier<?> key) {
        port.decrement(key, tags);
    }

    public Integer getCurrentCount(final Identifier<?> key) {
        return port.getCurrentCount(key, tags);
    }

    public interface Port {

        Boolean tryIncrement(Identifier<?> key, int maxConcurrent, String... tags);

        void increment(Identifier<?> key, String... tags);

        void decrement(Identifier<?> key, String... tags);

        Integer getCurrentCount(Identifier<?> key, String... tags);

    }

}
