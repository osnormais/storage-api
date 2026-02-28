package org.osnormais.storage.api.domain;

import org.osnormais.storage.api.domain.validation.ValidationHandler;

public interface ValueObject extends Validatable {

    default void validate(ValidationHandler handler) {
    };

}
