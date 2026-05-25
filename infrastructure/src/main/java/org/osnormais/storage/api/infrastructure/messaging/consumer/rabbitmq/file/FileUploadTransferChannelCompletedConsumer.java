package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.application.usecase.file.publish.PublishFileUseCase;
import org.osnormais.storage.api.domain.exception.FileAlreadyPublishedException;
import org.osnormais.storage.api.infrastructure.file.data.message.FileUploadTransferChannelCompletedMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class FileUploadTransferChannelCompletedConsumer
        extends RabbitMQMessageConsumer<FileUploadTransferChannelCompletedMessage> {

    private final PublishFileUseCase finalizeFileUseCase;

    public FileUploadTransferChannelCompletedConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<FileUploadTransferChannelCompletedMessage>> errorMessageProducer,
            final PublishFileUseCase finalizeFileUseCase) {
        super(maxRetryAttempts, errorMessageProducer, Set.of(FileAlreadyPublishedException.class));
        this.finalizeFileUseCase = requireNonNull(finalizeFileUseCase);
    }

    @Override
    public void consume(final Message<FileUploadTransferChannelCompletedMessage> message) {

        finalizeFileUseCase
                .execute(null);

    }

}
