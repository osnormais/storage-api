package org.osnormais.storage.api.infrastructure.exception;

public class UnexpectedException extends InfrastructureException {

    public UnexpectedException(final String message, final Throwable cause) {
        super(message, cause, false);
    }

    public static UnexpectedException with(final String message) {
        return new UnexpectedException(message, null);
    }

    public static UnexpectedException with(final Throwable cause) {
        return new UnexpectedException(cause.getMessage(), cause);
    }

    public static UnexpectedException with(final String message, final Throwable cause) {
        return new UnexpectedException(message, cause);
    }

}
