package org.osnormais.storage.api.infrastructure.exception;

public class ExceptionWrapper extends RuntimeException {

    private ExceptionWrapper(final Throwable cause) {
        super(null, cause, true, false);
    }

    public static RuntimeException wrap(final Throwable cause) {
        if (cause instanceof RuntimeException)
            return (RuntimeException) cause;

        return new ExceptionWrapper(cause);
    }

}
