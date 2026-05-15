package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class DefaultCreateFileUploadTransferChannelUseCase extends CreateFileUploadTransferChannelUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final FileCommandGateway fileCommandGateway;

    private final Size chunkSizeBytes;
    private final ParallelChunkLimit maxParallelChunks;
    private final ThroughputLimit maxBytesPerSecondPerChunk;

    public DefaultCreateFileUploadTransferChannelUseCase(
            final FileQueryGateway fileQueryGateway,
            final FileCommandGateway fileCommandGateway,
            final Long chunkSizeBytes,
            final Integer maxParallelChunks,
            final Long maxBytesPerSecondPerChunk) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.chunkSizeBytes = Size.of(requireNonNull(chunkSizeBytes));
        this.maxParallelChunks = ParallelChunkLimit.of(requireNonNull(maxParallelChunks));
        this.maxBytesPerSecondPerChunk = ThroughputLimit.create(requireNonNull(maxBytesPerSecondPerChunk));
    }

    @Transactional
    @Override
    public CreateFileUploadTransferChannelOutput execute(final CreateFileUploadTransferChannelInput input) {

        final FileId fileId = FileId.of(input.fileId());
        final ThroughputLimit targetRateLimit = ThroughputLimit.create(input.targetBytesPerSecond());

        final ValidationHandler handler = Notification.create();
        fileId.validate(handler);
        targetRateLimit.validate(handler);
        if (handler.hasErrors())
            throw ValidationException.with("Invalid input values", handler);

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        final TransferChannel transferChannel = file.openUploadChannel(
                targetRateLimit,
                maxBytesPerSecondPerChunk,
                maxParallelChunks,
                chunkSizeBytes);

        fileCommandGateway.update(file);

        return new CreateFileUploadTransferChannelOutput(
                file.getId().getValue(),
                transferChannel.getChunkSpecification().totalChunks(file.getSize()),
                transferChannel.getChunkSpecification().effectiveChunkSize(file.getSize()).bytes(),
                transferChannel.getChunkSpecification().lastChunkSize(file.getSize()).bytes());

    }

}
