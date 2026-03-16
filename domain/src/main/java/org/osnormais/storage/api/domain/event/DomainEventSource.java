package org.osnormais.storage.api.domain.event;

import java.util.Optional;

@FunctionalInterface
public interface DomainEventSource {

    Optional<DomainEvent> nextEvent();

}
