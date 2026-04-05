package org.osnormais.storage.api.infrastructure.configuration.domain.event;

import java.util.List;

import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.event.DomainEventHandler;
import org.osnormais.storage.api.infrastructure.event.outbox.dispatcher.OutboxEventDispatcher;
import org.osnormais.storage.api.infrastructure.event.outbox.gateway.OutboxJpaGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventDispatcherConfig {

    private final OutboxJpaGateway outboxGateway;
    private final ConcurrencyTracker.Port concurrencyTrackerPort;

    public DomainEventDispatcherConfig(
            final OutboxJpaGateway outboxGateway,
            final ConcurrencyTracker.Port concurrencyTrackerPort) {
        this.outboxGateway = outboxGateway;
        this.concurrencyTrackerPort = concurrencyTrackerPort;
    }

    @Bean
    DomainEventDispatcher eventDispatcher(final List<DomainEventHandler<?>> eventHandlers) {
        final var dispatcher = new OutboxEventDispatcher(
                outboxGateway,
                new ConcurrencyTracker(concurrencyTrackerPort, new String[] { "event-dispatcher" }));
        eventHandlers.forEach(handler -> dispatcher.register(handler.eventKey(), handler));
        return dispatcher;
    }

}
