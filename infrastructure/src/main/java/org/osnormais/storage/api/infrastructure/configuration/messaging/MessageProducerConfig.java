package org.osnormais.storage.api.infrastructure.configuration.messaging;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.infrastructure.file.data.message.domain.FileDomainEvent;
import org.osnormais.storage.api.infrastructure.file.data.message.integration.drive.DriveFileIntegrationMessage;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.osnormais.storage.api.infrastructure.messaging.producer.springcloud.SpringCloudMessageProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;

@Configuration
public class MessageProducerConfig {

    private final StreamBridge streamBridge;

    public MessageProducerConfig(final StreamBridge streamBridge) {
        this.streamBridge = requireNonNull(streamBridge);
    }

    @Bean
    @Primary
    MessageProducer<FileDomainEvent> fileUploadTransferChannelCompleted() {
        return new SpringCloudMessageProducer<>(streamBridge, "fileUploadTransferChannelCompleted-out-0");
    }

    @Bean
    MessageProducer<Message<FileDomainEvent>> fileUploadTransferChannelCompletedError() {
        return new SpringCloudMessageProducer<>(streamBridge, "fileUploadTransferChannelCompletedError-out-0");
    }

    @Bean
    MessageProducer<Message<DriveFileIntegrationMessage>> driveFileCreatedError() {
        return new SpringCloudMessageProducer<>(streamBridge, "driveFileCreatedError-out-0");
    }

}
