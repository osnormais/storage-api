package org.osnormais.storage.api.domain.validation;

import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

@FunctionalInterface
public interface Validatable {

    void validate(ValidationHandler handler);

}
