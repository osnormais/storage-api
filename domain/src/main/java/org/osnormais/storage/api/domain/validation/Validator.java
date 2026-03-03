package org.osnormais.storage.api.domain.validation;

import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public abstract class Validator {

    private final ValidationHandler handler;

    protected Validator(final ValidationHandler handler) {
        this.handler = handler;
    }

    public abstract void validate();

    protected ValidationHandler validationHandler() {
        return this.handler;
    }

}
