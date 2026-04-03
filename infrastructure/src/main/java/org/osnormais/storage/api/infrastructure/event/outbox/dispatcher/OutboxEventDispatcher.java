package org.osnormais.storage.api.infrastructure.event.outbox.dispatcher;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;

import org.osnormais.storage.api.domain.Identifier;
import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventContext;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.event.DomainEventSource;
import org.osnormais.storage.api.infrastructure.configuration.mapper.Mapper;
import org.osnormais.storage.api.infrastructure.event.outbox.gateway.OutboxJpaGateway;
import org.osnormais.storage.api.infrastructure.event.outbox.persistence.OutboxJpa;
import org.osnormais.storage.api.infrastructure.exception.ExceptionWrapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OutboxEventDispatcher extends DomainEventDispatcher {

    private final ObjectMapper mapper = Mapper.mapper();
    private final OutboxJpaGateway outboxGateway;

    public OutboxEventDispatcher(final OutboxJpaGateway outboxGateway) {
        this.outboxGateway = requireNonNull(outboxGateway);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public <I extends Identifier<?>> DomainEventContext append(
            final DomainEventContext context,
            final DomainEvent<I> event) {

        save(handlerFor(event.key())
                .stream()
                .map(outbox -> toOutboxJpa(context, event))
                .toList());

        return context;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public <I extends Identifier<?>> DomainEventContext append(
            final DomainEventContext context,
            final DomainEventSource source) {

        var event = source.nextEvent();
        var nextContext = context;

        while (event.isPresent()) {

            final var actualEvent = event.get();
            final var actualContext = nextContext;

            save(handlerFor(actualEvent.key())
                    .stream()
                    .map(outbox -> toOutboxJpa(actualContext, actualEvent))
                    .toList());

            event = source.nextEvent();
            nextContext = nextContext.createNext();

        }

        return nextContext;

    }

    @Override
    public void dispatch(DomainEventContext... contexts) {

        for (final var context : contexts) {

            final var outBoxEvents = outboxGateway
                    .findByContextId(context.id())
                    .stream()
                    .sorted(Comparator.comparing(OutboxJpa::getContextPosition));

            outBoxEvents.forEach(
                    event -> {

                        handlerFor(event.getEventKey())
                                .stream()
                                .filter(handler -> handler.supports(event.getEventKey()))
                                .forEach(handler -> handler.handle(convert(event)));

                        outboxGateway.delete(event.getId());

                    });

        }

    }

    private void save(final List<OutboxJpa> events) {
        outboxGateway.save(events);
    }

    private static OutboxJpa toOutboxJpa(
            final DomainEventContext context,
            final DomainEvent<?> event) {

        return new OutboxJpa(
                context.id(),
                context.position(),
                event.key(),
                event.getClass(),
                event);
    }

    @SuppressWarnings("unchecked")
    private <E extends DomainEvent<?>> E convert(final OutboxJpa event) {
        try {
            return mapper.convertValue(event.getPayload(), (Class<E>) event.getPayloadClass());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Failed to convert OutboxJpa to DomainEvent. payloadClass="
                            + event.getPayloadClass().getName(),
                    e);
        } catch (Exception e) {
            throw ExceptionWrapper.wrap(e);
        }
    }

}
