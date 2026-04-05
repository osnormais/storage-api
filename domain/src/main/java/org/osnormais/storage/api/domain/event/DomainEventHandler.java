package org.osnormais.storage.api.domain.event;

import static java.util.Objects.requireNonNull;

public abstract class DomainEventHandler<E extends DomainEvent<?>> {

    private final String eventKey;

    protected DomainEventHandler(final String eventKey) {
        this.eventKey = requireNonNull(eventKey);
    }

    public String eventKey() {
        return eventKey;
    }

    public boolean supports(final String eventKey) {
        return this.eventKey.equals(eventKey);
    }

    public abstract void handle(E event);

}
