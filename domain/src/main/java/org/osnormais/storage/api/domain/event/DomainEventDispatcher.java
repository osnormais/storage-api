package org.osnormais.storage.api.domain.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osnormais.storage.api.domain.Identifier;

public abstract class DomainEventDispatcher {

    protected final ConcurrentHashMap<String, List<DomainEventHandler<?>>> handlers = new ConcurrentHashMap<>();

    public abstract void dispatch(final DomainEventContext... context);

    public abstract <I extends Identifier<?>> DomainEventContext append(
            final DomainEventContext context,
            final DomainEvent<I> event);

    public abstract <I extends Identifier<?>> DomainEventContext append(
            final DomainEventContext context,
            final DomainEventSource source);

    public DomainEventContext append(final DomainEventSource source) {
        return append(DomainEventContext.create(), source);
    }

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

    protected List<DomainEventHandler<?>> handlerFor(final String eventKey) {
        return this.handlers.getOrDefault(eventKey, List.of());
    }

}
