package org.osnormais.storage.api.infrastructure.exception;

public class InvalidArgumentException extends InfrastructureException {

    public InvalidArgumentException(final String message) {
        super(message, null, false);
    }

    public static InvalidArgumentException with(final String message) {
        return new InvalidArgumentException(message);
    }

}
