package org.osnormais.storage.api.application.usecase.file.chunk.upload;

import java.io.InputStream;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.exception.ChunkIntegrityViolationException;
import org.osnormais.storage.api.application.exception.ConcurrentChunkLimitExceededException;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public class DefaultUploadFileChunkUseCase extends UploadFileChunkUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final ConcurrencyTracker concurrencyTracker;
    private final ChunkWriter chunkWriter;

    public DefaultUploadFileChunkUseCase(
            final FileQueryGateway fileQueryGateway,
            final ConcurrencyTracker concurrencyTracker,
            final ChunkWriter chunkWriter) {
        this.fileQueryGateway = fileQueryGateway;
        this.concurrencyTracker = concurrencyTracker;
        this.chunkWriter = chunkWriter;
    }

    @Transactional
    @Override
    public void execute(final UploadFileChunkInput input) {

        final FileId fileId = FileId.of(input.fileId());
        final Long chunkIndex = input.chunkIndex();
        final Long chunkSize = input.chunkSize();
        final Integer maxParallelChunks = input.maxParallelChunks();
        final Long throughputLimit = input.throughputLimit();
        final InputStream chunkData = input.chunkData();

        final Checksum checksum = Checksum.of(input.checksumAlgorithm(), input.checksumValue());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        if (file.isPublished())// TODO exception específica
            throw new RuntimeException(String.format("File with id %s is already published", fileId.getValue()));

        if (!concurrencyTracker.tryIncrement(fileId, maxParallelChunks))
            throw ConcurrentChunkLimitExceededException.create(maxParallelChunks);

        try {

            final Checksum writeChecksumResult = chunkWriter.writeChunk(
                    fileId,
                    chunkIndex,
                    chunkSize,
                    throughputLimit,
                    checksum.algorithm(),
                    chunkData);

            if (!writeChecksumResult.equals(checksum))
                throw ChunkIntegrityViolationException.create(checksum, writeChecksumResult);

        } finally {
            concurrencyTracker.decrement(fileId);
        }

    }

}
