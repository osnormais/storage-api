package org.osnormais.storage.api.infrastructure.file.event;

import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.event.DomainEventHandler;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.event.FilePublicationFinalizedEvent;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.storage.StorageFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.FilePublicationFinalizedIntegrationProducer;
import org.springframework.stereotype.Component;

@Component
public class FilePublicationFinalizedEventHandler extends DomainEventHandler<FilePublicationFinalizedEvent> {

    private final FileQueryGateway fileQueryGateway;
    private final FilePublicationFinalizedIntegrationProducer filePublicationFinalizedIntegrationProducer;

    FilePublicationFinalizedEventHandler(
            final FileQueryGateway fileQueryGateway,
            final FilePublicationFinalizedIntegrationProducer filePublicationFinalizedIntegrationProducer) {
        super(FilePublicationFinalizedEvent.eventKey());
        this.fileQueryGateway = fileQueryGateway;
        this.filePublicationFinalizedIntegrationProducer = filePublicationFinalizedIntegrationProducer;
    }

    @Override
    public void handle(final FilePublicationFinalizedEvent event) {
        final File file = fileQueryGateway.findById(event.getIdentifier()).orElseThrow();
        filePublicationFinalizedIntegrationProducer.produce(StorageFileIntegrationMessage.from(file));
    }

}
