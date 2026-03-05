package org.osnormais.storage.api.domain.file.valueobject;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record ThroughputLimit(long bytesPerSecond) implements ValueObject {

    @Override
    public void validate(final ValidationHandler handler) {

        if (bytesPerSecond < 0)
            handler.append(ValidationError.with("bytesPerSecond must be greater than or equal to 0"));

    }

}
