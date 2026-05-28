package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.command.CreateFileCommand;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class CreateFileCommandProducer extends SpringCloudMessageProducer<CreateFileCommand> {

    private static final String BINDING_NAME = "createFileCommand-out-0";

    public CreateFileCommandProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
