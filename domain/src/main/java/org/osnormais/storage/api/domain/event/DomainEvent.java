package org.osnormais.storage.api.domain.event;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osnormais.storage.api.domain.Entity;
import org.osnormais.storage.api.domain.Identifier;

public abstract class DomainEvent<I extends Identifier<?>> {

    private static final String DOMAIN = "storage";

    private I identifier;
    private String domain;
    private String entity;
    private String action;
    private Instant occurredAt;
    private Set<DomainEventEntity> relatedEntities;

    protected DomainEvent() {
    }

    protected <E extends Entity<I>> DomainEvent(
            final E entity,
            final String subResource,
            final String action,
            final Instant occurredAt,
            final Collection<DomainEventEntity> relatedEntities) {

        this.identifier = entity.getId();
        this.domain = DOMAIN;
        this.entity = DomainEvent.entity(entity.getClass(), subResource);
        this.action = action;
        this.occurredAt = occurredAt;
        this.relatedEntities = isNull(relatedEntities) ? Set.of()
                : relatedEntities
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
    }

    protected static String key(
            final Class<? extends Entity<?>> entityClass,
            final String subResource,
            final String action) {
        return DOMAIN
                + "."
                + DomainEvent.entity(entityClass, subResource)
                + "."
                + action;
    }

    private static String entity(final Class<?> entityClass, final String subResource) {
        return (entityClass.getSimpleName() + (isNull(subResource) ? "" : ":" + subResource.trim())).toLowerCase();
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
        return isNull(relatedEntities) ? Set.of() : Set.copyOf(relatedEntities);
    }

}
