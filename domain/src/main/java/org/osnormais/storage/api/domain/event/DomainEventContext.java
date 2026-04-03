package org.osnormais.storage.api.domain.event;

import java.util.UUID;

public record DomainEventContext(UUID id, Long position) {

    public static DomainEventContext create() {
        return new DomainEventContext(UUID.randomUUID(), 0L);
    }

    public DomainEventContext createNext() {
        return new DomainEventContext(id, position + 1);
    }

}
