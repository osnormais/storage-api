package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DriveFileCreatedIntegrationErrorProducer
        extends SpringCloudMessageProducer<Message<DriveFileIntegrationMessage>> {

    private static final String BINDING_NAME = "driveFileCreatedIntegrationError-out-0";

    public DriveFileCreatedIntegrationErrorProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
