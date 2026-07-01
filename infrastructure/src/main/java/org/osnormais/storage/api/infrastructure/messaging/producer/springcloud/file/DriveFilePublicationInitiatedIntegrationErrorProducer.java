package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileEventMessage;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DriveFilePublicationInitiatedIntegrationErrorProducer
        extends SpringCloudMessageProducer<Message<DriveFileEventMessage>> {

    private static final String BINDING_NAME = "driveFilePublicationInitiatedIntegrationError-out-0";

    public DriveFilePublicationInitiatedIntegrationErrorProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
