package org.osnormais.storage.api.domain;

import org.osnormais.storage.api.domain.validation.ValidationHandler;

@FunctionalInterface
public interface Validatable {

    void validate(ValidationHandler handler);

}
