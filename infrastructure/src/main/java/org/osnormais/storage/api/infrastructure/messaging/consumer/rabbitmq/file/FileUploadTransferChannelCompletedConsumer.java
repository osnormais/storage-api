package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import java.util.Set;

import org.osnormais.storage.api.infrastructure.file.data.message.FileUploadTransferChannelCompletedMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class FileUploadTransferChannelCompletedConsumer
        extends RabbitMQMessageConsumer<FileUploadTransferChannelCompletedMessage> {

    public FileUploadTransferChannelCompletedConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<FileUploadTransferChannelCompletedMessage> errorMessageProducer) {
        super(maxRetryAttempts, errorMessageProducer, Set.of());
    }

    @Override
    public void consume(final Message<FileUploadTransferChannelCompletedMessage> message) {
        System.out.println("Received message: " + message.getPayload());
    }

}
