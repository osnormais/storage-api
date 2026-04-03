package org.osnormais.storage.api.application.usecase.file.create;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.commons.annotation.Transactional;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class DefaultCreateFileUseCase extends CreateFileUseCase {

    private final FileCommandGateway fileCommandGateway;

    public DefaultCreateFileUseCase(final FileCommandGateway fileCommandGateway) {
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
    }

    @Transactional
    @Override
    public CreateFileOutput execute(final CreateFileInput input) {

        final FileId fileId = FileId.of(input.id());
        final Size fileSize = Size.of(input.sizeInBytes());
        final Checksum fileChecksum = Checksum.of(input.checksumAlgorithm(), input.checksumValue());

        final ValidationHandler handler = Notification.create();

        final File file = handler.validate(() -> File.create(fileId, fileSize, fileChecksum));

        if (handler.hasErrors())
            throw ValidationException.with("Failed to create File", handler);

        fileCommandGateway.create(file);

        return CreateFileOutput.of(file);

    }

}
