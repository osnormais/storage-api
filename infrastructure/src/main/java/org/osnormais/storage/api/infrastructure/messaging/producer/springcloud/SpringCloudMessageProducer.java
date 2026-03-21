package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;

public class SpringCloudMessageProducer<T> implements MessageProducer<T> {

    private final StreamBridge streamBridge;
    private final String bindingName;

    public SpringCloudMessageProducer(
            final StreamBridge streamBridge,
            final String bindingName) {
        this.streamBridge = requireNonNull(streamBridge);
        this.bindingName = requireNonNull(bindingName);
    }

    @Override
    public void produce(final T payload) {
        streamBridge.send(bindingName, payload);
    }

}