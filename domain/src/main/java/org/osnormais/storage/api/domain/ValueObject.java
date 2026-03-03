package org.osnormais.storage.api.domain;

import org.osnormais.storage.api.domain.validation.Validatable;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public interface ValueObject extends Validatable {

    default void validate(ValidationHandler handler) {
    };

}
