package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.file;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.osnormais.storage.api.application.usecase.file.create.CreateFileInput;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.infrastructure.exception.ResourceAlreadyExistsException;
import org.osnormais.storage.api.infrastructure.file.data.message.command.CreateFileCommand;
import org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq.RabbitMQMessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;

public class CreateFileCommandConsumer extends RabbitMQMessageConsumer<CreateFileCommand> {

    private final CreateFileUseCase createFileUseCase;

    public CreateFileCommandConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<CreateFileCommand>> errorMessageProducer,
            final CreateFileUseCase createFileUseCase) {
        super(maxRetryAttempts, errorMessageProducer, Set.of(ResourceAlreadyExistsException.class));
        this.createFileUseCase = requireNonNull(createFileUseCase);
    }

    @Override
    public void consume(final Message<CreateFileCommand> message) {

        createFileUseCase
                .execute(new CreateFileInput(
                        message.getPayload().id(),
                        message.getPayload().sizeInBytes(),
                        message.getPayload().checksumValue(),
                        message.getPayload().checksumAlgorithm()));

    }

}
