package org.osnormais.storage.api.infrastructure.configuration.messaging;

import java.util.function.Consumer;

import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.publish.PublishFileUseCase;
import org.osnormais.storage.api.infrastructure.file.data.message.command.CreateFileCommand;
import org.osnormais.storage.api.infrastructure.file.data.message.command.PublishFileCommand;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.CreateFileCommandConsumer;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.DriveFileCreatedIntegrationConsumer;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.DriveFilePublicationInitiatedIntegrationConsumer;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.PublishFileCommandConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.CreateFileCommandErrorProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.CreateFileCommandProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.DriveFileCreatedIntegrationErrorProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.DriveFilePublicationInitiatedIntegrationErrorProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.PublishFileCommandErrorProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.PublishFileCommandProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class MessageConsumerConfig {

    @Bean
    Consumer<Message<DriveFileIntegrationMessage>> driveFileCreatedIntegrationConsumer(
            @Value("${application.messaging.public.consumer.drive-file-created.max-attempts}") final Long maxAttempts,
            DriveFileCreatedIntegrationErrorProducer errorMessageProducer,
            final CreateFileCommandProducer createFileCommandProducer) {
        return new DriveFileCreatedIntegrationConsumer(
                maxAttempts,
                errorMessageProducer,
                createFileCommandProducer);
    }

    @Bean
    Consumer<Message<DriveFileIntegrationMessage>> driveFilePublicationInitiatedIntegrationConsumer(
            @Value("${application.messaging.public.consumer.drive-file-publication-initiated.max-attempts}") final Long maxAttempts,
            DriveFilePublicationInitiatedIntegrationErrorProducer errorMessageProducer,
            final PublishFileCommandProducer publishFileCommandProducer) {
        return new DriveFilePublicationInitiatedIntegrationConsumer(
                maxAttempts,
                errorMessageProducer,
                publishFileCommandProducer);
    }

    @Bean
    Consumer<Message<CreateFileCommand>> createFileCommandConsumer(
            @Value("${application.messaging.private.consumer.create-file-command.max-attempts}") final Long maxAttempts,
            CreateFileCommandErrorProducer errorMessageProducer,
            final CreateFileUseCase createFileUseCase) {
        return new CreateFileCommandConsumer(
                maxAttempts,
                errorMessageProducer,
                createFileUseCase);
    }

    @Bean
    Consumer<Message<PublishFileCommand>> publishFileCommandConsumer(
            @Value("${application.messaging.private.consumer.publish-file-command.max-attempts}") final Long maxAttempts,
            PublishFileCommandErrorProducer errorMessageProducer,
            final PublishFileUseCase publishFileUseCase) {
        return new PublishFileCommandConsumer(
                maxAttempts,
                errorMessageProducer,
                publishFileUseCase);
    }

}
