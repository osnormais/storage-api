package org.osnormais.storage.api.domain.event;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import org.osnormais.storage.api.domain.Entity;
import org.osnormais.storage.api.domain.Identifier;

public abstract class DomainEvent<I extends Identifier<?>> {

    private static final String DOMAIN = "storage";

    private final I identifier;
    private final String domain;
    private final String entity;
    private final String action;
    private final Instant occurredAt;
    private final Set<DomainEventEntity> relatedEntities;

    protected <E extends Entity<I>> DomainEvent(
            final E entity,
            final String subResource,
            final String action,
            final Instant occurredAt,
            final Set<DomainEventEntity> relatedEntities) {
        this.identifier = entity.getId();
        this.domain = DOMAIN;
        this.entity = (entity.getClass().getSimpleName() + (isNull(subResource) ? "" : ":" + subResource.trim()))
                .toLowerCase();
        this.action = action;
        this.occurredAt = occurredAt;
        this.relatedEntities = relatedEntities;
    }

    protected static String key(
            final Class<? extends Entity<?>> entity,
            final String subResource,
            final String action) {
        return DOMAIN
                + "."
                + (entity.getSimpleName() + (isNull(subResource) ? "" : ":" + subResource.trim()))
                        .toLowerCase()
                + "."
                + action;
    }

    public String key() {
        return domain + "." + entity + "." + action;
    }

    public I getIdentifier() {
        return identifier;
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
