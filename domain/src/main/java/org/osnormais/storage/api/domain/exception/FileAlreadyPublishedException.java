package org.osnormais.storage.api.domain.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class FileAlreadyPublishedException extends SilentDomainException {

    private static final String MESSAGE = "File [%s], already published";

    private FileAlreadyPublishedException(final FileId fileId) {
        super(
                MESSAGE.formatted(fileId.getStringValue()),
                List.of());
    }

    public static FileAlreadyPublishedException create(final File file) {
        return new FileAlreadyPublishedException(file.getId());
    }

}
