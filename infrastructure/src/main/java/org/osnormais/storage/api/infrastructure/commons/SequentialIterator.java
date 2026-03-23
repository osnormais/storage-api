package org.osnormais.storage.api.infrastructure.commons;

import static java.util.Objects.isNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.osnormais.storage.api.infrastructure.exception.InvalidArgumentException;

public final class SequentialIterator<T> implements Iterator<T> {

    private final ConcurrentHashMap<Long, T> items;
    private AtomicLong actualPosition;

    private SequentialIterator(final Set<Item<T>> items) {

        validate(items);

        this.items = new ConcurrentHashMap<>(
                items
                        .stream()
                        .collect(Collectors
                                .toMap(
                                        item -> item.position,
                                        item -> item.value)));

        this.actualPosition = new AtomicLong(
                items
                        .stream()
                        .map(Item::position)
                        .min(Comparator.naturalOrder())
                        .orElse(0L));

    }

    public static <T> SequentialIterator<T> of(final Set<Item<T>> items) {
        return new SequentialIterator<>(items);
    }

    public boolean hasNext() {
        return items.containsKey(actualPosition.get());
    }

    public T next() {
        final Long position = actualPosition.getAndIncrement();
        final T item = items.get(position);
        if (isNull(item))
            throw new IllegalStateException("No more items available at position: " + position);
        return item;
    }

    public Long currentPosition() {
        return actualPosition.longValue();
    }

    public static record Item<T>(T value, Long position) {

        public static <T> Item<T> of(final T value, final Long position) {
            return new Item<>(value, position);
        }

    }

    private static <T> void validate(final Set<Item<T>> items) {

        if (isNull(items) || items.isEmpty())
            throw InvalidArgumentException.with("Items cannot be null or empty.");

        final List<Long> positions = items.stream()
                .map(Item::position)
                .sorted()
                .toList();

        for (int i = 1; i < positions.size(); i++) {
            final long expected = positions.get(i - 1) + 1;
            if (positions.get(i) != expected) {
                throw InvalidArgumentException.with(
                        "Invalid sequence. Expected position "
                                + expected
                                + " but found "
                                + positions.get(i));
            }
        }

    }

}
