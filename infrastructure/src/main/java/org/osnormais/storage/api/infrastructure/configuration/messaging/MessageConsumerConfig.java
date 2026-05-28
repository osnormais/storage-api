package org.osnormais.storage.api.infrastructure.configuration.messaging;

import java.util.function.Consumer;

import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.infrastructure.file.data.message.command.CreateFileCommand;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.CreateFileCommandConsumer;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file.DriveFileCreatedIntegrationConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.CreateFileCommandErrorProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.CreateFileCommandProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.file.DriveFileCreatedIntegrationErrorConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class MessageConsumerConfig {

    @Bean
    Consumer<Message<DriveFileIntegrationMessage>> driveFileCreatedIntegrationConsumer(
            @Value("${application.messaging.public.consumer.drive-file-created.max-attempts}") final Long maxAttempts,
            DriveFileCreatedIntegrationErrorConsumer errorMessageProducer,
            final CreateFileCommandProducer createFileCommandProducer) {
        return new DriveFileCreatedIntegrationConsumer(
                maxAttempts,
                errorMessageProducer,
                createFileCommandProducer);
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

}
