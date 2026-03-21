package org.osnormais.storage.api.infrastructure.file.event;

import org.osnormais.storage.api.domain.event.DomainEventHandler;
import org.osnormais.storage.api.domain.file.event.FileUploadTransferChannelCompletedEvent;
import org.osnormais.storage.api.infrastructure.file.data.message.FileUploadTransferChannelCompletedMessage;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.stereotype.Component;

@Component
public class FileUploadTransferChannelCompletedEventHandler
        extends DomainEventHandler<FileUploadTransferChannelCompletedEvent> {

    private final MessageProducer<FileUploadTransferChannelCompletedMessage> messageProducer;

    public FileUploadTransferChannelCompletedEventHandler(
            final MessageProducer<FileUploadTransferChannelCompletedMessage> messageProducer) {
        super(FileUploadTransferChannelCompletedEvent.eventKey());
        this.messageProducer = messageProducer;
    }

    @Override
    public void handle(final FileUploadTransferChannelCompletedEvent event) {
        messageProducer.produce(new FileUploadTransferChannelCompletedMessage(event.getIdentifier().getValue()));
    }

}