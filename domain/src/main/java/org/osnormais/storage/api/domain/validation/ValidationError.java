package org.osnormais.storage.api.domain.validation;

import org.osnormais.storage.api.domain.exception.DomainException;

public record ValidationError(String message) {

    public static ValidationError with(final String message) {
        return new ValidationError(message);
    }

    public static ValidationError fromDomainError(final DomainException.Error domainError) {
        return new ValidationError(domainError.message());
    }

    public DomainException.Error toDomainError() {
        return DomainException.Error.with(message());
    }

}
