package org.osnormais.storage.api.domain.exception;

import java.util.List;

import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class ValidationException extends SilentDomainException {

    private ValidationException(final String message, final List<DomainException.Error> errors) {
        super(message, List.copyOf(errors));
    }

    public static ValidationException with(final String message, final ValidationHandler handler) {
        return new ValidationException(
                message,
                handler
                        .getErrors()
                        .stream()
                        .map(ValidationError::toDomainError)
                        .toList());
    }

    public static ValidationException with(final String message, final DomainException.Error error) {
        return new ValidationException(message, List.of(error));
    }

    public static ValidationException with(final String message, final ValidationError error) {
        return new ValidationException(message, List.of(error.toDomainError()));
    }

    public static ValidationException with(final String message, final List<ValidationError> errors) {
        return new ValidationException(
                message,
                errors
                        .stream()
                        .map(ValidationError::toDomainError)
                        .toList());
    }

}
