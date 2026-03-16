package org.osnormais.storage.api.domain.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DomainEventDispatcher {

    private final ConcurrentHashMap<String, List<DomainEventHandler>> handlers = new ConcurrentHashMap<>();

    public void register(final String eventKey, final DomainEventHandler handler) {
        this.handlers.computeIfAbsent(eventKey, k -> new ArrayList<>()).add(handler);
    }

    public void unregister(final DomainEvent event, final DomainEventHandler handler) {
        this.handlers.computeIfPresent(event.key(), (k, v) -> {
            v.remove(handler);
            return v.isEmpty() ? null : v;
        });
    }

    public void unregisterAll(final String eventKey) {
        this.handlers.remove(eventKey);
    }

    public void notify(final DomainEvent event) {
        @SuppressWarnings("unchecked")
        final List<DomainEventHandler> handlers = (List<DomainEventHandler>) Optional
                .ofNullable(this.handlers.get(event.key()))
                .filter(h -> !h.isEmpty())
                .map(h -> (List<DomainEventHandler>) (List<?>) h)
                .orElse(List.of());

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
