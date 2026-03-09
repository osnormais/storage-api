package org.osnormais.storage.api.domain.file.valueobject;

import static java.util.Objects.isNull;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record TransferChannel(
        ThroughputLimit throughputLimit,
        ChunkSpecification chunkSpecification) implements ValueObject {

    public static TransferChannel create(
            final ThroughputLimit throughputLimit,
            final ChunkSpecification chunkSpecification) {
        return new TransferChannel(throughputLimit, chunkSpecification);
    }

    @Override
    public void validate(final ValidationHandler handler) {

        if (isNull(throughputLimit))
            handler.append(ValidationError.with("'throughputLimit' should not be null"));
        else
            throughputLimit.validate(handler);

        if (isNull(chunkSpecification))
            handler.append(ValidationError.with("'chunkSpecification' should not be null"));
        else
            chunkSpecification.validate(handler);

    }

}