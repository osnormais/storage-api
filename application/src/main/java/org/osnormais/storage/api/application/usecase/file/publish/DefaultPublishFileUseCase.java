package org.osnormais.storage.api.application.usecase.file.publish;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class DefaultPublishFileUseCase extends PublishFileUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final FileCommandGateway fileCommandGateway;
    private final FileFinalizer fileFinalizer;
    private final DomainEventDispatcher eventDispatcher;

    public DefaultPublishFileUseCase(
            final FileQueryGateway fileQueryGateway,
            final FileCommandGateway fileCommandGateway,
            final FileFinalizer fileFinalizer,
            final DomainEventDispatcher eventDispatcher) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.fileFinalizer = requireNonNull(fileFinalizer);
        this.eventDispatcher = requireNonNull(eventDispatcher);
    }

    @Transactional
    @Override
    public void execute(final PublishFileInput input) {

        final FileId fileId = FileId.of(input.fileId());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        file.publish(() -> fileFinalizer.finalize(file, input.chunkSizeInBytes()));

        fileCommandGateway.update(file);

        eventDispatcher.dispatch(eventDispatcher.append(file));

    }

}
