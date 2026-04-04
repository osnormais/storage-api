package org.osnormais.storage.api.infrastructure.exception;

public abstract class InfrastructureException extends RuntimeException {

    public InfrastructureException(final String message, final Throwable cause, final Boolean suppress) {
        super(message, cause, suppress, !suppress);
    }

}
