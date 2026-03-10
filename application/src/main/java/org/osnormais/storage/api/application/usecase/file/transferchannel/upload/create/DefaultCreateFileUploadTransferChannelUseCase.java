package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class DefaultCreateFileUploadTransferChannelUseCase extends CreateFileUploadTransferChannelUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final FileCommandGateway fileCommandGateway;

    public DefaultCreateFileUploadTransferChannelUseCase(
            final FileQueryGateway fileQueryGateway,
            final FileCommandGateway fileCommandGateway) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
    }

    @Override
    public CreateFileUploadTransferChannelOutput execute(final CreateFileUploadTransferChannelInput input) {

        final ValidationHandler handler = Notification.create();

        final FileId fileId = FileId.of(input.fileId());
        final TransferChannel transferChannel = createTransferChannel(input);

        fileId.validate(handler);
        transferChannel.validate(handler);

        if (handler.hasErrors())
            throw ValidationException.with("Invalid input values", handler);

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        handler.validate(() -> file.openUploadChannel(transferChannel));

        if (handler.hasErrors())
            throw ValidationException.with("Failed to open upload transfer channel", handler);

        fileCommandGateway.update(file);

        return new CreateFileUploadTransferChannelOutput(
                file.getId().getValue(),
                transferChannel.chunkSpecification().totalChunks(file.getSize()),
                transferChannel.chunkSpecification().chunkBytesSize(file.getSize()).bytes(),
                transferChannel.chunkSpecification().lastChunkSize(file.getSize()).bytes());

    }

    private static TransferChannel createTransferChannel(final CreateFileUploadTransferChannelInput input) {

        final ThroughputLimit throughputLimit = ThroughputLimit.create(input.throughputBytesLimit());

        final Size chunkSpecificationSize = Size.of(input.chunkBytesSize());
        final ParallelChunkLimit chunkSpecificationParallelChunkLimit = ParallelChunkLimit
                .of(input.maxParallelChunks());

        final ChunkSpecification chunkSpecification = ChunkSpecification
                .create(
                        chunkSpecificationSize,
                        chunkSpecificationParallelChunkLimit);

        return TransferChannel.create(throughputLimit, chunkSpecification);

    }

}
