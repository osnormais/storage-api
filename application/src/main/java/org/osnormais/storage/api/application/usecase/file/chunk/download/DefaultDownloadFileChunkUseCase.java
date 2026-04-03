package org.osnormais.storage.api.application.usecase.file.chunk.download;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.exception.ConcurrentChunkLimitExceededException;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.exception.TransferChannelNotAvailableException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

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
        final Long chunkIndex = input.chunkIndex();

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        final TransferChannel downloadChannel = file
                .getDownloadChannel()
                .orElseThrow(() -> TransferChannelNotAvailableException.download(fileId));

        final Size fileSize = file.getSize();
        final Size chunkSize = downloadChannel.getChunkSpecification().effectiveChunkSize(fileSize, chunkIndex);
        final Long chunkOffset = downloadChannel.getChunkSpecification().chunkOffset(fileSize, chunkIndex);
        final ParallelChunkLimit maxParallelChunks = downloadChannel.getChunkSpecification().maxParallel();
        final ThroughputLimit throughputLimit = downloadChannel.getThroughputLimit();

        if (maxParallelChunks.value() <= concurrencyTracker.getCurrentCount(fileId))
            throw ConcurrentChunkLimitExceededException.create(maxParallelChunks);

        concurrencyTracker.increment(fileId);

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
