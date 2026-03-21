package org.osnormais.storage.api.domain.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osnormais.storage.api.domain.Identifier;

public final class DomainEventDispatcher {

    private final ConcurrentHashMap<String, List<DomainEventHandler<?>>> handlers = new ConcurrentHashMap<>();

    public void register(final String eventKey, final DomainEventHandler<?> handler) {
        this.handlers.computeIfAbsent(eventKey, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public void unregister(final String eventKey, final DomainEventHandler<?> handler) {
        this.handlers.computeIfPresent(eventKey, (k, v) -> {
            v.remove(handler);
            return v.isEmpty() ? null : v;
        });
    }

    public void unregisterAll(final String eventKey) {
        this.handlers.remove(eventKey);
    }

    @SuppressWarnings("unchecked")
    public <I extends Identifier<?>> void notify(final DomainEvent<I> event) {

        this.handlers
                .getOrDefault(event.key(), List.of())
                .stream()
                .filter(handler -> handler.eventKey().equals(event.key()))
                .map(handler -> (DomainEventHandler<DomainEvent<I>>) handler)
                .forEach(handler -> handler.handle(event));

    }

    public void notify(final DomainEventSource source) {
        var event = source.nextEvent();
        while (event.isPresent()) {
            notify(event.get());
            event = source.nextEvent();
        }
    }

}
