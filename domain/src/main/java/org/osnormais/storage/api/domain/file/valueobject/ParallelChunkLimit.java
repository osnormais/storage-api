package org.osnormais.storage.api.domain.file.valueobject;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record ParallelChunkLimit(int value) implements ValueObject {

    @Override
    public void validate(final ValidationHandler handler) {

        if (value < 1)
            handler.append(ValidationError.with("'value' must be greater than or equal to 1"));

    }

}
