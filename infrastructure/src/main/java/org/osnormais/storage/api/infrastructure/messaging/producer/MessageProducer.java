package org.osnormais.storage.api.infrastructure.messaging.producer;

@FunctionalInterface
public interface MessageProducer<T> {

    void produce(T payload);

}
