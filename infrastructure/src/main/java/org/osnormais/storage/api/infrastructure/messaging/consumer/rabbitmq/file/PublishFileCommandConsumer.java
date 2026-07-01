package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.application.usecase.file.publish.PublishFileInput;
import org.osnormais.storage.api.application.usecase.file.publish.PublishFileUseCase;
import org.osnormais.storage.api.infrastructure.exception.ResourceAlreadyExistsException;
import org.osnormais.storage.api.infrastructure.file.data.message.command.PublishFileCommand;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class PublishFileCommandConsumer extends RabbitMQMessageConsumer<PublishFileCommand> {

    private final PublishFileUseCase publishFileUseCase;

    public PublishFileCommandConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<PublishFileCommand>> errorMessageProducer,
            final PublishFileUseCase publishFileUseCase) {
        super(maxRetryAttempts, errorMessageProducer, Set.of(ResourceAlreadyExistsException.class));
        this.publishFileUseCase = requireNonNull(publishFileUseCase);
    }

    @Override
    public void consume(final Message<PublishFileCommand> message) {
        publishFileUseCase.execute(new PublishFileInput(message.getPayload().id()));
    }

}
