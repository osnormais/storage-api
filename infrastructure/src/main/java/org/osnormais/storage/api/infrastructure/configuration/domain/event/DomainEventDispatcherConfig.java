package org.osnormais.storage.api.infrastructure.configuration.domain.event;

import java.util.List;

import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.event.DomainEventHandler;
import org.osnormais.storage.api.infrastructure.event.outbox.dispatcher.OutboxEventDispatcher;
import org.osnormais.storage.api.infrastructure.event.outbox.gateway.OutboxJpaGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventDispatcherConfig {

    private final OutboxJpaGateway outboxGateway;

    public DomainEventDispatcherConfig(final OutboxJpaGateway outboxGateway) {
        this.outboxGateway = outboxGateway;
    }

    @Bean
    DomainEventDispatcher eventDispatcher(final List<DomainEventHandler<?>> eventHandlers) {
        final var dispatcher = new OutboxEventDispatcher(outboxGateway);
        eventHandlers.forEach(handler -> dispatcher.register(handler.eventKey(), handler));
        return dispatcher;
    }

}
