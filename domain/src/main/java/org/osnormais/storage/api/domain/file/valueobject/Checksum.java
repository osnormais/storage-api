package org.osnormais.storage.api.domain.file.valueobject;

import static java.util.Objects.isNull;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record Checksum(Algorithm algorithm, String value) implements ValueObject {

    public enum Algorithm {

        CRC_32,
        MD5,
        SHA_256;

    }

    @Override
    public void validate(ValidationHandler handler) {
        if (isNull(algorithm))
            handler.append(new ValidationError("checksum algorithm cant be null"));

        if (isNull(value))
            handler.append(new ValidationError("checksum value cant be null"));

    }

}
