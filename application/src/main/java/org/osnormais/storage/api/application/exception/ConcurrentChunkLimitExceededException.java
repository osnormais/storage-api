package org.osnormais.storage.api.application.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;

public class ConcurrentChunkLimitExceededException extends SilentApplicationException {

    private static final String DEFAULT_MESSAGE = "Maximum parallel chunk uploads reached for the file.";

    private ConcurrentChunkLimitExceededException(final ParallelChunkLimit parallelChunkLimit) {
        super(
                DEFAULT_MESSAGE,
                List.of(
                        ApplicationException.Error
                                .with(
                                        "Maximum parallel chunk uploads of "
                                                + parallelChunkLimit.value()
                                                + " exceeded for the file.")));
    }

    public static ConcurrentChunkLimitExceededException create(final ParallelChunkLimit parallelChunkLimit) {
        return new ConcurrentChunkLimitExceededException(parallelChunkLimit);
    }

}
