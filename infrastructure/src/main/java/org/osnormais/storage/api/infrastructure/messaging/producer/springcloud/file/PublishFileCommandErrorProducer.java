package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.command.PublishFileCommand;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class PublishFileCommandErrorProducer extends SpringCloudMessageProducer<Message<PublishFileCommand>> {

    private static final String BINDING_NAME = "publishFileCommandError-out-0";

    public PublishFileCommandErrorProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
