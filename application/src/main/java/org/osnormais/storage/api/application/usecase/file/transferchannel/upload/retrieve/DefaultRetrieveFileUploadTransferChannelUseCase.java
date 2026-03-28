package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.exception.TransferChannelNotAvailableException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;

public class DefaultRetrieveFileUploadTransferChannelUseCase extends RetrieveFileUploadTransferChannelUseCase {

    private final FileQueryGateway fileQueryGateway;

    public DefaultRetrieveFileUploadTransferChannelUseCase(final FileQueryGateway fileQueryGateway) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
    }

    @Override
    public RetrieveFileUploadTransferChannelOutput execute(final RetrieveFileUploadTransferChannelInput input) {

        final FileId fileId = FileId.of(input.fileId());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        final TransferChannel uploadChannel = file
                .getUploadChannel()
                .orElseThrow(() -> TransferChannelNotAvailableException.upload(fileId));

        return new RetrieveFileUploadTransferChannelOutput(
                file.getId().getValue(),
                uploadChannel.getStatus(),
                uploadChannel.getThroughputLimit().bytesPerSecond(),
                uploadChannel.getChunkSpecification().totalChunks(file.getSize()),
                uploadChannel.getChunkSpecification().effectiveChunkSize(file.getSize()).bytes(),
                uploadChannel.getChunkSpecification().lastChunkSize(file.getSize()).bytes(),
                uploadChannel.getChunkSpecification().maxParallel().value());

    }

}

