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
    @Test
    void givenNullId_whenValidate_thenHandlerShouldAppendError() {

        final var expectedErrorsCount = 1;
        final var expectedHasErrors = true;
        final var expectedErrorMessage = "id cant be null";

        final FileId expectedFileId = null;
        final var expectedSize = 2L;

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = new Checksum(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedFile = new File(expectedFileId, expectedSize, expectedChecksum);

        final var handler = Notification.create();

        expectedFile.validate(handler);

        final var actualHasErrors = handler.hasErrors();
        final var actualErrors = handler.getErrors();

        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedHasErrors, actualHasErrors);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());
    }
    
    @Test
    void givenNullChecksum_whenValidate_thenHandlerShouldAppendError() {

        final var expectedErrorsCount = 1;
        final var expectedHasErrors = true;
        final var expectedErrorMessage = "checksum cant be null";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = new FileId(expectedIdValue);
        final var expectedSize = 2L;

        final Checksum expectedChecksum = null;

        final var expectedFile = new File(expectedFileId, expectedSize, expectedChecksum);

        final var handler = Notification.create();

        expectedFile.validate(handler);

        final var actualHasErrors = handler.hasErrors();
        final var actualErrors = handler.getErrors();

        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedHasErrors, actualHasErrors);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());
    }
    @Test
    void givenValidFile_whenValidate_thenHandlerShouldNotAppendError() { 
        final var expectedErrorsCount = 0;
        final var expectedHasErrors = false;
        final var expectedErrorMessage = "";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = new FileId(expectedIdValue);
        final var expectedSize = 2L;

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = new Checksum(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedFile = new File(expectedFileId, expectedSize, expectedChecksum);

        final var handler = Notification.create();

        expectedFile.validate(handler);

        final var actualHasErrors = handler.hasErrors();
        final var actualErrors = handler.getErrors();

        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedHasErrors, actualHasErrors);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());


    }
}