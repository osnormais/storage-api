package org.osnormais.storage.api.application.usecase.file.chunk.upload;

import java.io.InputStream;

import org.osnormais.storage.api.application.exception.ChunkIntegrityViolationException;
import org.osnormais.storage.api.application.exception.ConcurrentChunkLimitExceededException;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.exception.TransferChannelNotAvailableException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

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

    @Override
    public void execute(final UploadFileChunkInput input) {

        final FileId fileId = FileId.of(input.fileId());
        final Long chunkIndex = input.chunkIndex();
        final InputStream chunkData = input.chunkData();

        final Checksum checksum = Checksum.of(input.checksumAlgorithm(), input.checksumValue());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        final TransferChannel uploadChannel = file
                .getUploadChannel()
                .orElseThrow(() -> TransferChannelNotAvailableException.upload(fileId));

        final Size chunkSize = uploadChannel.getChunkSpecification().effectiveChunkSize(file.getSize(), chunkIndex);
        final ParallelChunkLimit maxParallelChunks = uploadChannel.getChunkSpecification().maxParallel();
        final ThroughputLimit throughputLimit = uploadChannel.getThroughputLimit();

        if (maxParallelChunks.value() <= concurrencyTracker.getCurrentCount(fileId))
            throw ConcurrentChunkLimitExceededException.create(maxParallelChunks);

        concurrencyTracker.increment(fileId);

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
