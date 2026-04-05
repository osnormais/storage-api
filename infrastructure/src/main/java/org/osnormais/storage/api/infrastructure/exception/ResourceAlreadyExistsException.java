package org.osnormais.storage.api.infrastructure.exception;

public class ResourceAlreadyExistsException extends InfrastructureException {

    public ResourceAlreadyExistsException(final String message) {
        super(message, null, true);
    }

    public static ResourceAlreadyExistsException with(final String message) {
        return new ResourceAlreadyExistsException(message);
    }

}
