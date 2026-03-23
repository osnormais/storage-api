package org.osnormais.storage.api.application.usecase.file.finalize;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Publication;

public class DefaultFinalizeFileUseCase extends FinalizeFileUseCase {

    private final FileQueryGateway fileQueryGateway;
    private final FileCommandGateway fileCommandGateway;
    private final FileFinalizer fileFinalizer;

    public DefaultFinalizeFileUseCase(
            final FileQueryGateway fileQueryGateway,
            final FileCommandGateway fileCommandGateway,
            final FileFinalizer fileFinalizer) {
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.fileFinalizer = requireNonNull(fileFinalizer);
    }

    @Override
    public void execute(final FinalizeFileInput input) {

        final FileId fileId = FileId.of(input.fileId());

        final File file = fileQueryGateway
                .findById(fileId)
                .orElseThrow(() -> NotFoundException.create(File.class, fileId));

        if (file.isPublished())
            return;

        final Checksum checksum = fileFinalizer.finalize(file);
        if (file.getChecksum().equals(checksum))
            file.completePublication();
        else
            file.failPublication(
                    Publication.Error.of(
                            "File integrity compromised during finalization. Expected checksum: %s, actual checksum: %s"
                                    .formatted(file.getChecksum(), checksum)));

        fileCommandGateway.update(file);

    }

}
