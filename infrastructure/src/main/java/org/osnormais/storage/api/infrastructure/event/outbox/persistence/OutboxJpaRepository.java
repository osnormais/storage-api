package org.osnormais.storage.api.infrastructure.event.outbox.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.osnormais.storage.api.domain.event.DomainEventContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxJpaRepository extends JpaRepository<OutboxJpa, UUID> {

    List<OutboxJpa> findByContextId(UUID contextId);

    @Query("""
            select
                new org.osnormais.storage.api.domain.event.DomainEventContext(o.contextId, min(o.contextPosition))
            from Outbox o
            where o.processed = false
              and o.registeredAt <= :cutoff
            group by o.contextId
            order by min(o.registeredAt) asc
                     """)
    List<DomainEventContext> findAllContextPending(@Param("cutoff") Instant cutoff);

}
