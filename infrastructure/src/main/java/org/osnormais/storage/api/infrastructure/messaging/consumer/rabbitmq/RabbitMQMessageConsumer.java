package org.osnormais.storage.api.infrastructure.messaging.consumer.rabbitmq;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osnormais.storage.api.infrastructure.messaging.consumer.MessageConsumer;
import org.osnormais.storage.api.infrastructure.messaging.producer.MessageProducer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

public abstract class RabbitMQMessageConsumer<T extends Serializable> extends MessageConsumer<Message<T>> {

    protected RabbitMQMessageConsumer(
            final Long maxRetryAttempts,
            final MessageProducer<Message<T>> errorMessageProducer,
            final Set<Class<? extends Throwable>> unretryableExceptions) {
        super(new RabbitMQMessageConsumer.RabbitMQFailureHandler<T>(
                maxRetryAttempts,
                unretryableExceptions,
                errorMessageProducer));
    }

    static class RabbitMQFailureHandler<T extends Serializable> implements FailureHandler<Message<T>> {

        private final Long maxRetryAttempts;
        private final Set<Class<? extends Throwable>> unretryableExceptions;

        private final MessageProducer<Message<T>> errorMessageProducer;

        RabbitMQFailureHandler(
                final Long maxRetryAttempts,
                final Set<Class<? extends Throwable>> unretryableExceptions,
                final MessageProducer<Message<T>> errorMessageProducer) {
            this.maxRetryAttempts = maxRetryAttempts;
            this.unretryableExceptions = unretryableExceptions;
            this.errorMessageProducer = errorMessageProducer;
        }

        @Override
        public void handle(final Message<T> message, final Throwable throwable) {

            if (isMaxRetryAttemptsExceeded(getRetryCount(message.getHeaders()))) {
                errorMessageProducer.produce(MessageBuilder.fromMessage(message).build());
                return;
            }

            if (isExceptionRetryable(throwable))
                throw RetryableException.of(throwable);

            errorMessageProducer.produce(MessageBuilder.fromMessage(message).build());
        }

        private Boolean isMaxRetryAttemptsExceeded(final Long actualRetryCount) {
            return maxRetryAttempts.compareTo(actualRetryCount) <= 0;
        }

        private Boolean isExceptionRetryable(final Throwable throwable) {
            return !unretryableExceptions.contains(throwable.getClass());
        }

        @SuppressWarnings("unchecked")
        private static Long getRetryCount(final MessageHeaders headers) {

            final List<Map<String, Object>> xDeath = (List<Map<String, Object>>) headers.get("x-death");
            if (xDeath != null && !xDeath.isEmpty()) {
                final Map<String, Object> deathInfo = xDeath.get(0);
                final Long count = (Long) deathInfo.get("count");
                return count != null ? count : 0L;
            }

            return 0L;
        }

    }

}
