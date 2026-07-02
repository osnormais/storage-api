package org.osnormais.storage.api.application.usecase.file.chunk.download;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.exception.ConcurrentChunkLimitExceededException;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class DefaultDownloadFileChunkUseCase extends DownloadFileChunkUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final ConcurrencyTracker concurrencyTracker;
    private final ChunkReader chunkReader;

    public DefaultDownloadFileChunkUseCase(
            final FileQueryGateway fileQueryGateway,
            final ConcurrencyTracker concurrencyTracker,
            final ChunkReader chunkReader) {
        this.fileQueryGateway = fileQueryGateway;
        this.concurrencyTracker = concurrencyTracker;
        this.chunkReader = chunkReader;
    }

    @Transactional
    @Override
    public DownloadFileChunkOutput execute(final DownloadFileChunkInput input) {

        final FileId fileId = FileId.of(input.fileId());
        final Long chunkSize = input.chunkSize();
        final Long chunkOffset = input.chunkOffset();
        final Integer maxParallelChunks = input.maxParallelChunks();
        final Long throughputLimit = input.throughputLimit();

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        if (!file.isPublished())// TODO exception específica
            throw new RuntimeException(String.format("File with id %s is not published yet", fileId.getValue()));

        if (!concurrencyTracker.tryIncrement(fileId, maxParallelChunks))
            throw ConcurrentChunkLimitExceededException.create(maxParallelChunks);

        try {

            return new DownloadFileChunkOutput(
                    chunkReader.readChunk(
                            fileId,
                            chunkOffset,
                            chunkSize,
                            throughputLimit));

        } finally {
            concurrencyTracker.decrement(fileId);
        }

    }

}
