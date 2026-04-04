package org.osnormais.storage.api.infrastructure.event.outbox.relay;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.osnormais.storage.api.infrastructure.event.outbox.dispatcher.OutboxEventDispatcher;
import org.osnormais.storage.api.infrastructure.event.outbox.gateway.OutboxJpaGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxRelayWorker {

    private final OutboxEventDispatcher dispatcher;
    private final OutboxJpaGateway outboxGateway;

    public OutboxRelayWorker(
            final OutboxEventDispatcher dispatcher,
            final OutboxJpaGateway outboxGateway) {
        this.dispatcher = requireNonNull(dispatcher);
        this.outboxGateway = requireNonNull(outboxGateway);
    }

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void retryPendingOutboxEvents() {

        outboxGateway
                .findAllContextPending(Instant.now().minus(5l, ChronoUnit.MINUTES))
                .forEach(dispatcher::dispatch);

    }

}
