package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.infrastructure.file.data.message.command.PublishFileCommand;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class DriveFilePublicationInitiatedIntegrationConsumer
        extends RabbitMQMessageConsumer<DriveFileIntegrationMessage> {

    private final MessageProducer<PublishFileCommand> publishFileCommandProducer;

    public DriveFilePublicationInitiatedIntegrationConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<DriveFileIntegrationMessage>> errorMessageProducer,
            final MessageProducer<PublishFileCommand> publishFileCommandProducer) {
        super(maxRetryAttempts, errorMessageProducer, Set.of());
        this.publishFileCommandProducer = requireNonNull(publishFileCommandProducer);
    }

    @Override
    public void consume(final Message<DriveFileIntegrationMessage> message) {
        publishFileCommandProducer.produce(new PublishFileCommand(message.getPayload().id()));
    }
}
