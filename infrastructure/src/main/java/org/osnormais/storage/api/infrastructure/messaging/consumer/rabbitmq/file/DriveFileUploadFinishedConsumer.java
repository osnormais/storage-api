package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.application.usecase.file.publish.PublishFileInput;
import org.osnormais.storage.api.application.usecase.file.publish.PublishFileUseCase;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileUploadFinishedMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class DriveFileUploadFinishedConsumer extends RabbitMQMessageConsumer<DriveFileUploadFinishedMessage> {

    private final PublishFileUseCase publishFileUseCase;

    public DriveFileUploadFinishedConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<DriveFileUploadFinishedMessage>> errorMessageProducer,
            final PublishFileUseCase publishFileUseCase) {
        super(maxRetryAttempts, errorMessageProducer, Set.of());
        this.publishFileUseCase = requireNonNull(publishFileUseCase);
    }

    @Override
    public void consume(final Message<DriveFileUploadFinishedMessage> message) {

        publishFileUseCase.execute(new PublishFileInput(
                message.getPayload().fileId(),
                message.getPayload().chunkSizeInBytes()));

    }

}
