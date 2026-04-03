package org.osnormais.storage.api.infrastructure.event.outbox.persistence;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Outbox")
@Table(name = "outbox")
public class OutboxJpa {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID contextId;

    @Column(nullable = false)
    private Long contextPosition;

    @Column(nullable = false)
    private String eventKey;

    @Column(nullable = false)
    private Class<?> payloadClass;

    @JdbcTypeCode(SqlTypes.JSON)
    private Object payload;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant registeredAt;

    @Column(nullable = false)
    private Boolean processed;

    public OutboxJpa(
            final UUID contextId,
            final Long contextPosition,
            final String eventKey,
            final Class<?> payloadClass,
            final Object payload) {
        this.contextId = contextId;
        this.contextPosition = contextPosition;
        this.eventKey = eventKey;
        this.payloadClass = payloadClass;
        this.payload = payload;
        this.processed = Boolean.FALSE;
    }

    public OutboxJpa() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getContextId() {
        return contextId;
    }

    public void setContextId(UUID contextId) {
        this.contextId = contextId;
    }

    public Long getContextPosition() {
        return contextPosition;
    }

    public void setContextPosition(Long contextPosition) {
        this.contextPosition = contextPosition;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public Class<?> getPayloadClass() {
        return payloadClass;
    }

    public void setPayloadClass(Class<?> payloadClass) {
        this.payloadClass = payloadClass;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OutboxJpa other = (OutboxJpa) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
