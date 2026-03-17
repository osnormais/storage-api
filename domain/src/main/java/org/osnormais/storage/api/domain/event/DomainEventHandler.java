package org.osnormais.storage.api.domain.event;

public abstract class DomainEventHandler {

    private final String eventKey;

    protected DomainEventHandler(final String eventKey) {
        this.eventKey = eventKey;
    }

    public String eventKey() {
        return eventKey;
    }

    public abstract void handle(DomainEvent<?> event);

}
