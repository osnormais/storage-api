package org.osnormais.storage.api.application.exception;

import java.util.List;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public class ChunkIntegrityViolationException extends SilentApplicationException {

    private static final String DEFAULT_MESSAGE = "Chunk integrity violation.";

    private ChunkIntegrityViolationException(final Checksum expected, final Checksum actual) {
        super(
                DEFAULT_MESSAGE,
                List.of(
                        ApplicationException.Error
                                .with(
                                        "Chunk integrity violation: expected "
                                                + expected
                                                + ", but got "
                                                + actual)));
    }

    public static ChunkIntegrityViolationException create(final Checksum expected, final Checksum actual) {
        return new ChunkIntegrityViolationException(expected, actual);
    }

}
