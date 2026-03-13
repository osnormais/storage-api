package org.osnormais.storage.api.application.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.FileId;

public class TrasnferChannelNotAvailableException extends SilentApplicationException {

    private static final String DEFAULT_MESSAGE = "Trasnfer channel is not available.";

    private static final String UPLOAD_TYPE = "upload";

    private TrasnferChannelNotAvailableException(final FileId fileId, final String type) {
        super(
                DEFAULT_MESSAGE,
                List.of(
                        ApplicationException.Error.with(
                                "["
                                        + type
                                        + "]"
                                        + "Trasnfer channel of File=["
                                        + fileId.getStringValue()
                                        + "] is not available")));
    }

    public static TrasnferChannelNotAvailableException upload(final FileId fileId) {
        return new TrasnferChannelNotAvailableException(fileId, UPLOAD_TYPE);
    }

}