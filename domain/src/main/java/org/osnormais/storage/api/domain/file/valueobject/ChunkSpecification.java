package org.osnormais.storage.api.domain.file.valueobject;

import org.osnormais.storage.api.domain.ValueObject;

public record ChunkSpecification(
        Size size,
        ParallelChunkLimit maxParallel) implements ValueObject {

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

}
