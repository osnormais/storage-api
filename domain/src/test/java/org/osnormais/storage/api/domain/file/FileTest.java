package org.osnormais.storage.api.domain.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class FileTest {

    @Test
    void givenNegativeSize_whenValidate_thenHandlerShouldAppendError() {

        final var expectedErrorsCount = 1;
        final var expectedHasErrors = true;
        final var expectedErrorMessage = "size must be greater than or equal to 0";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = new FileId(expectedIdValue);
        final var expectedSize = -2L;

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = new Checksum(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedFile = new File(expectedFileId, expectedSize, expectedChecksum);

        final var handler = Notification.create();

        expectedFile.validate(handler);

        final var actualHasErros = handler.hasErrors();
        final var actualErrors = handler.getErrors();

        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedHasErrors, actualHasErros);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());

    }
}
