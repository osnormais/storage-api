package org.osnormais.storage.api.domain.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class FileUploadInProgressException extends SilentDomainException {

    private static final String MESSAGE = "Upload transfer channel openned for this file [%s], upload in progress";
    private static final String ERROR = MESSAGE + ", please close the current channel before publishing the file";

    private FileUploadInProgressException(final FileId fileId) {
        super(
                MESSAGE.formatted(fileId.getStringValue()),
                List.of(DomainException.Error.with(ERROR.formatted(fileId.getStringValue()))));
    }

    public static FileUploadInProgressException create(final File file) {
        return new FileUploadInProgressException(file.getId());
    }

}
