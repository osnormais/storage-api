package org.osnormais.storage.api.domain.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class FileNotYetPublished extends SilentDomainException {

    private static final String MESSAGE = "File [%s], not yet published";

    private FileNotYetPublished(final FileId fileId) {
        super(
                MESSAGE.formatted(fileId.getStringValue()),
                List.of());
    }

    public static FileNotYetPublished create(final File file) {
        return new FileNotYetPublished(file.getId());
    }

}
