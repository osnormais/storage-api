package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.infrastructure.file.data.message.command.CreateFileCommand;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class DriveFileCreatedIntegrationConsumer extends RabbitMQMessageConsumer<DriveFileIntegrationMessage> {

    private final MessageProducer<CreateFileCommand> createFileCommandProducer;

    public DriveFileCreatedIntegrationConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<DriveFileIntegrationMessage>> errorMessageProducer,
            final MessageProducer<CreateFileCommand> createFileCommandProducer) {
        super(maxRetryAttempts, errorMessageProducer, Set.of());
        this.createFileCommandProducer = requireNonNull(createFileCommandProducer);
    }

    @Override
    public void consume(final Message<DriveFileIntegrationMessage> message) {

        createFileCommandProducer.produce(
                new CreateFileCommand(
                        message.getPayload().id(),
                        message.getPayload().size(),
                        message.getPayload().checksumValue(),
                        message.getPayload().checksumAlgorithm()));

    }

}
