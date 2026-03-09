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

    public static Checksum of(final String value, final Algorithm algorithm) {
        return new Checksum(algorithm, value);
    }

    @Override
    public void validate(final ValidationHandler handler) {

        if (isNull(algorithm))
            handler.append(new ValidationError("'algorithm' should not be null"));

        if (isNull(value))
            handler.append(new ValidationError("'value' should not be null"));

    }

}
