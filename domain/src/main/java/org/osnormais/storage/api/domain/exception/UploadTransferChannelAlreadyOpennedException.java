package org.osnormais.storage.api.domain.exception;

import java.util.List;

public class UploadTransferChannelAlreadyOpennedException extends SilentDomainException {

    private static final String MESSAGE = "Upload transfer channel already open";
    private static final String ERROR = MESSAGE + ", please close the current channel before opening a new one";

    private UploadTransferChannelAlreadyOpennedException() {
        super(MESSAGE, List.of(Error.with(ERROR)));
    }

    public static UploadTransferChannelAlreadyOpennedException create() {
        return new UploadTransferChannelAlreadyOpennedException();
    }

}
