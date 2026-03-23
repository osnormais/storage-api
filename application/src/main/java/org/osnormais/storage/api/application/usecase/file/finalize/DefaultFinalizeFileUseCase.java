package org.osnormais.storage.api.application.usecase.file.finalize;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public class DefaultFinalizeFileUseCase extends FinalizeFileUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final FileCommandGateway fileCommandGateway;
    private final FileFinalizer fileFinalizer;
    private final DomainEventDispatcher eventDispatcher;

    public DefaultFinalizeFileUseCase(
            final FileQueryGateway fileQueryGateway,
            final FileCommandGateway fileCommandGateway,
            final FileFinalizer fileFinalizer,
            final DomainEventDispatcher eventDispatcher) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.fileFinalizer = requireNonNull(fileFinalizer);
        this.eventDispatcher = requireNonNull(eventDispatcher);
    }

    @Override
    public void execute(final FinalizeFileInput input) {

        final FileId fileId = FileId.of(input.fileId());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId))
                .validateCanBeFinalized();

        if (file.isPublished())
            return;

        final Checksum checksum = fileFinalizer.finalize(file);
        file.finalizePublication(checksum);
        eventDispatcher.notify(fileCommandGateway.update(file));

    }

}
