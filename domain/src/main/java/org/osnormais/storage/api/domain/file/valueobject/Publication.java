package org.osnormais.storage.api.domain.file.valueobject;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Optional;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record Publication(
        Instant publishedAt,
        Publication.Status status,
        Optional<Publication.Error> error) implements ValueObject {

    public static Publication ok() {
        return new Publication(Instant.now(), Status.OK, Optional.empty());
    }

    public static Publication error(final Publication.Error error) {
        return new Publication(Instant.now(), Status.ERROR, Optional.of(error));
    }

    @Override
    public void validate(final ValidationHandler handler) {

        if (isNull(publishedAt))
            handler.append(new ValidationError("Publication.publishedAt should not be null"));

        if (isNull(status))
            handler.append(new ValidationError("Publication.status should not be null"));

        if (isNull(error) && Status.ERROR.equals(status))
            handler.append(new ValidationError("Publication.error should not be null when status is ERROR"));
        else if (Status.ERROR.equals(status))
            error.ifPresent(e -> e.validate(handler));

        if (Status.OK.equals(status) && error.isPresent())
            handler.append(new ValidationError("Publication.error should be empty when status is OK"));

    }

    public enum Status {
        OK, ERROR
    }

    public record Error(String message) implements ValueObject {

        public static Error of(final String message) {
            return new Error(message);
        }

        @Override
        public void validate(final ValidationHandler handler) {

            if (isNull(message))
                handler.append(new ValidationError("Publication.Error.message should not be null"));

            if (!isNull(message) && message.isBlank())
                handler.append(new ValidationError("Publication.Error.message should not be blank"));

        }

    }

}
