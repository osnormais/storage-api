package org.osnormais.storage.api.domain.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DomainEventDispatcher {

    private final ConcurrentHashMap<String, List<DomainEventHandler>> handlers = new ConcurrentHashMap<>();

    public void register(final String eventKey, final DomainEventHandler handler) {
        this.handlers.computeIfAbsent(eventKey, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public void unregister(final String eventKey, final DomainEventHandler handler) {
        this.handlers.computeIfPresent(eventKey, (k, v) -> {
            v.remove(handler);
            return v.isEmpty() ? null : v;
        });
    }

    public void unregisterAll(final String eventKey) {
        this.handlers.remove(eventKey);
    }

    public void notify(final DomainEvent<?> event) {
        final List<DomainEventHandler> handlers = this.handlers.getOrDefault(event.key(), List.of());
        handlers.forEach(handler -> handler.handle(event));
    }

    public void notify(final DomainEventSource source) {
        var event = source.nextEvent();
        while (event.isPresent()) {
            notify(event.get());
            event = source.nextEvent();
        }
    }

}
