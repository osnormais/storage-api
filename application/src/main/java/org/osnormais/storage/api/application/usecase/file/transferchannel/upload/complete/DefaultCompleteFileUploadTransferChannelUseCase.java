package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.complete;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class DefaultCompleteFileUploadTransferChannelUseCase extends CompleteFileUploadTransferChannelUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final FileCommandGateway fileCommandGateway;

    private final DomainEventDispatcher eventDispatcher;

    public DefaultCompleteFileUploadTransferChannelUseCase(
            final FileQueryGateway fileQueryGateway,
            final FileCommandGateway fileCommandGateway,
            final DomainEventDispatcher eventDispatcher) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.eventDispatcher = requireNonNull(eventDispatcher);
    }

    @Transactional
    @Override
    public void execute(final CompleteFileUploadTransferChannelInput input) {

        final FileId fileId = FileId.of(input.fileId());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        file.completeUploadChannel();

        eventDispatcher.dispatch(eventDispatcher.append(fileCommandGateway.update(file)));

    }

}
