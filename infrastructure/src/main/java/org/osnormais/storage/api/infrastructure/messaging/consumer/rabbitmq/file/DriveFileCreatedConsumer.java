package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.application.usecase.file.create.CreateFileInput;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.infrastructure.exception.ResourceAlreadyExistsException;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class DriveFileCreatedConsumer
        extends RabbitMQMessageConsumer<DriveFileIntegrationMessage> {

    private final CreateFileUseCase createFileUseCase;

    public DriveFileCreatedConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<DriveFileIntegrationMessage>> errorMessageProducer,
            final CreateFileUseCase createFileUseCase) {
        super(maxRetryAttempts, errorMessageProducer, Set.of(ResourceAlreadyExistsException.class));
        this.createFileUseCase = requireNonNull(createFileUseCase);
    }

    @Override
    public void consume(final Message<DriveFileIntegrationMessage> message) {

        createFileUseCase
                .execute(new CreateFileInput(
                        message.getPayload().id(),
                        message.getPayload().size(),
                        message.getPayload().checksumValue(),
                        message.getPayload().checksumAlgorithm()));

    }

}
