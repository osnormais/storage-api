package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.command.CreateFileCommand;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class CreateFileCommandErrorProducer extends SpringCloudMessageProducer<Message<CreateFileCommand>> {

    private static final String BINDING_NAME = "createFileCommandError-out-0";

    public CreateFileCommandErrorProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
