package org.osnormais.storage.api.domain.exception;

import static java.util.Objects.isNull;

import java.util.List;

public class InvalidArgumentException extends SilentDomainException {

    private static final String MESSAGE = "Invalid argument provided.";

    protected InvalidArgumentException(final List<DomainException.Error> errors) {
        super(MESSAGE, isNull(errors) ? List.of() : List.copyOf(errors));
    }

    public static InvalidArgumentException with(final DomainException.Error error) {
        final List<DomainException.Error> list = isNull(error) ? List.of() : List.of(error);
        return new InvalidArgumentException(list);
    }

    public static InvalidArgumentException with(final List<DomainException.Error> errors) {
        return new InvalidArgumentException(errors);
    }

}
