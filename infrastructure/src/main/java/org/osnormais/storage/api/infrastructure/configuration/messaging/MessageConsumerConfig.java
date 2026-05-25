package org.osnormais.storage.api.infrastructure.configuration.messaging;

import java.util.function.Consumer;

import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.publish.PublishFileUseCase;
import org.osnormais.storage.api.infrastructure.file.data.message.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.file.data.message.FileUploadTransferChannelCompletedMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.DriveFileCreatedConsumer;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.FileUploadTransferChannelCompletedConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class MessageConsumerConfig {

    @Bean
    Consumer<Message<FileUploadTransferChannelCompletedMessage>> fileUploadTransferChannelCompletedConsumer(
            @Value("${application.messaging.private.consumer.file-upload-transfer-channel-completed.max-attempts}") final Long maxAttempts,
            @Qualifier("fileUploadTransferChannelCompletedError") final MessageProducer<Message<FileUploadTransferChannelCompletedMessage>> errorMessageProducer,
            final PublishFileUseCase finalizeFileUseCase) {
        return new FileUploadTransferChannelCompletedConsumer(
                maxAttempts,
                errorMessageProducer,
                finalizeFileUseCase);
    }

    @Bean
    Consumer<Message<DriveFileIntegrationMessage>> driveFileCreatedConsumer(
            @Value("${application.messaging.public.consumer.drive-file-created.max-attempts}") final Long maxAttempts,
            @Qualifier("driveFileCreatedError") final MessageProducer<Message<DriveFileIntegrationMessage>> errorMessageProducer,
            final CreateFileUseCase createFileUseCase) {
        return new DriveFileCreatedConsumer(
                maxAttempts,
                errorMessageProducer,
                createFileUseCase);
    }

}
