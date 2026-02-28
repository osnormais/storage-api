package org.osnormais.storage.api.domain.file;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.ValidationHandler;

public record Checksum(Algorithm algorithm, String value) implements ValueObject {

    public enum Algorithm {

        CRC_32,
        MD5,
        SHA_256;

    }

    @Override
    public void validate(ValidationHandler handler) {
        if (algorithm == null)
            handler.append(new ValidationError("checksum algorithm cant be null"));

        if (value == null)
            handler.append(new ValidationError("checksum value cant be null"));

    }

}
