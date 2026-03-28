package org.osnormais.storage.api.domain.file.valueobject;

import static java.util.Objects.isNull;

import org.osnormais.storage.api.domain.ValueObject;
import org.osnormais.storage.api.domain.exception.DomainException;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public record ChunkSpecification(
        Size size,
        ParallelChunkLimit maxParallel) implements ValueObject {

    public static ChunkSpecification create(
            Size size,
            ParallelChunkLimit maxParallel) {
        return new ChunkSpecification(size, maxParallel);
    }

    @Override
    public void validate(final ValidationHandler handler) {

        if (isNull(size))
            handler.append(ValidationError.with("Chunk 'size' should not be null"));
        else
            size.validate(handler);

        if (isNull(maxParallel))
            handler.append(ValidationError.with("Chunk 'maxParallel' should not be null"));
        else
            maxParallel.validate(handler);

    }

    public Size effectiveChunkSize(final Size fileSize) {
        return size.bytes() > fileSize.bytes() ? fileSize : size;
    }

    public Size effectiveChunkSize(final Size fileSize, final Long chunkIndex) {

        if (isNull(fileSize))
            throw InvalidArgumentException
                    .with(DomainException.Error.with("'fileSize' should not be null"));

        if (isNull(chunkIndex))
            throw InvalidArgumentException
                    .with(DomainException.Error.with("'chunkIndex' should not be null"));

        final Long totalChunks = totalChunks(fileSize);

        if (chunkIndex < 0 || chunkIndex >= totalChunks)
            throw InvalidArgumentException
                    .with(DomainException.Error
                            .with("'chunkIndex' out of bounds"));

        return (chunkIndex == totalChunks - 1)
                ? lastChunkSize(fileSize)
                : effectiveChunkSize(fileSize);

    }

    public Size lastChunkSize(final Size fileSize) {

        final long hasPartialChunk = fileSize.bytes() % size.bytes() != 0 ? 1 : 0;
        final long lastChunkSize = hasPartialChunk == 1 ? fileSize.bytes() % size.bytes() : size.bytes();

        return new Size(lastChunkSize);
    }

    public Long totalChunks(final Size fileSize) {

        final long fullChunks = fileSize.bytes() / size.bytes();
        final long hasPartialChunk = fileSize.bytes() % size.bytes() != 0 ? 1 : 0;

        return fullChunks + hasPartialChunk;
    }

    public Long chunkOffset(final Size fileSize, final Long chunkIndex) {

        if (isNull(fileSize))
            throw InvalidArgumentException.with(DomainException.Error.with("'fileSize' should not be null"));

        if (isNull(chunkIndex))
            throw InvalidArgumentException.with(DomainException.Error.with("'chunkIndex' should not be null"));

        final Long totalChunks = totalChunks(fileSize);

        if (chunkIndex < 0 || chunkIndex >= totalChunks)
            throw InvalidArgumentException.with(DomainException.Error.with("'chunkIndex' out of bounds"));

        return (chunkIndex == totalChunks - 1) ? fileSize.bytes() - lastChunkSize(fileSize).bytes()
                : (chunkIndex * size.bytes());

    }

}
