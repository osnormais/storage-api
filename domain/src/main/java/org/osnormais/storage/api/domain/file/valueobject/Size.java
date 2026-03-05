package org.osnormais.storage.api.domain.file.valueobject;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record Size(long bytes) implements ValueObject {

    @Override
    public void validate(final ValidationHandler handler) {

        if (bytes <= 0)
            handler.append(ValidationError.with("bytes must be greater than 0"));

    }

}
