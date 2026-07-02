package org.osnormais.storage.api.application.exception;

import java.util.List;

public class ConcurrentChunkLimitExceededException extends SilentApplicationException {

    private static final String DEFAULT_MESSAGE = "Maximum parallel chunk reached for the file.";

    private ConcurrentChunkLimitExceededException(final Integer parallelChunkLimit) {
        super(
                DEFAULT_MESSAGE,
                List.of(ApplicationException.Error.with(
                        "Maximum parallel chunk of "
                                + parallelChunkLimit
                                + " exceeded for the file.")));
    }

    public static ConcurrentChunkLimitExceededException create(final Integer parallelChunkLimit) {
        return new ConcurrentChunkLimitExceededException(parallelChunkLimit);
    }

}
