package org.osnormais.storage.api.infrastructure.event.outbox.gateway;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.osnormais.storage.api.domain.event.DomainEventContext;
import org.osnormais.storage.api.infrastructure.event.outbox.persistence.OutboxJpa;
import org.osnormais.storage.api.infrastructure.event.outbox.persistence.OutboxJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxJpaGateway {

    private final OutboxJpaRepository outboxRepository;

    public OutboxJpaGateway(final OutboxJpaRepository outboxRepository) {
        this.outboxRepository = requireNonNull(outboxRepository);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void save(final List<OutboxJpa> outboxes) {
        outboxRepository.saveAll(outboxes);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OutboxJpa> findByContextId(final UUID contextId) {
        return outboxRepository.findByContextId(contextId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(final UUID id) {
        outboxRepository.deleteById(id);
    }

    public List<DomainEventContext> findAllContextPending(final Instant cutoff) {
        return outboxRepository.findAllContextPending(cutoff);
    }

}
