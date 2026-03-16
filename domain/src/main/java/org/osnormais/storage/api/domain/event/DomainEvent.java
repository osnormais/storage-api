package org.osnormais.storage.api.domain.event;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import org.osnormais.storage.api.domain.Entity;

public abstract class DomainEvent {

    private static final String DOMAIN = "storage";

    private final String domain;
    private final String entity;
    private final String action;
    private final Instant occurredAt;
    private final Set<DomainEventEntity> relatedEntities;

    protected <E extends Entity<?>> DomainEvent(
            final Class<E> entityClass,
            final String subResource,
            final String action,
            final Instant occurredAt,
            final Set<DomainEventEntity> relatedEntities) {
        this.domain = DOMAIN;
        this.entity = (entityClass.getSimpleName() + (isNull(subResource) ? "" : ":" + subResource.trim()))
                .toLowerCase();
        this.action = action;
        this.occurredAt = occurredAt;
        this.relatedEntities = relatedEntities;
    }

    public String key() {
        return domain + "." + entity + "." + action;
    }

    public String getDomain() {
        return domain;
    }

    public String getEntity() {
        return entity;
    }

    public String getAction() {
        return action;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Set<DomainEventEntity> getRelatedEntities() {
        return Objects.isNull(relatedEntities) ? Set.of() : Set.copyOf(relatedEntities);
    }

}
