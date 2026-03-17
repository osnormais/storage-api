package org.osnormais.storage.api.application.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.FileId;

public class TransferChannelNotAvailableException extends SilentApplicationException {

    private static final String DEFAULT_MESSAGE = "Transfer channel is not available.";

    private static final String UPLOAD_TYPE = "upload";

    private TransferChannelNotAvailableException(final FileId fileId, final String type) {
        super(
                DEFAULT_MESSAGE,
                List.of(
                        ApplicationException.Error.with(
                                "["
                                        + type
                                        + "]"
                                        + "Transfer channel of File=["
                                        + fileId.getStringValue()
                                        + "] is not available")));
    }

    public static TransferChannelNotAvailableException upload(final FileId fileId) {
        return new TransferChannelNotAvailableException(fileId, UPLOAD_TYPE);
    }

}