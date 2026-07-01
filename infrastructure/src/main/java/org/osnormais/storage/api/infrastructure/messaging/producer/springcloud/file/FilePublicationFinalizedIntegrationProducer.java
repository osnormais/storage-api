package org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file;

import org.osnormais.storage.api.infrastructure.file.data.message.integration.storage.StorageFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class FilePublicationFinalizedIntegrationProducer
        extends SpringCloudMessageProducer<StorageFileIntegrationMessage> {

    private static final String BINDING_NAME = "filePublicationFinalizedIntegration-out-0";

    public FilePublicationFinalizedIntegrationProducer(final StreamBridge streamBridge) {
        super(streamBridge, BINDING_NAME);
    }

}
