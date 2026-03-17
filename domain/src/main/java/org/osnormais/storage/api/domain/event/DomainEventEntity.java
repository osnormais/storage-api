package org.osnormais.storage.api.domain.event;

import org.osnormais.storage.api.domain.Entity;

public record DomainEventEntity(String type, String id) {

    public static DomainEventEntity of(final Entity<?> entity) {
        return new DomainEventEntity(entity.getClass().getSimpleName(), entity.getId().getStringValue());
    }

}
