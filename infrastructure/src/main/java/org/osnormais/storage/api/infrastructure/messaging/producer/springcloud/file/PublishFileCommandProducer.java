package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.command.PublishFileCommand;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class PublishFileCommandProducer extends SpringCloudMessageProducer<PublishFileCommand> {

    private static final String BINDING_NAME = "publishFileCommand-out-0";

    public PublishFileCommandProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
