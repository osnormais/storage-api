package org.osnormais.storage.api.domain.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class FileTest {

    @Test
    void givenNegativeSize_whenValidate_thenHandlerShouldAppendError() {

        final var expectedExceptionMessage = "'File' validation failed";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "bytes must be greater than 0";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = new FileId(expectedIdValue);
        final var expectedSize = new Size(-2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = new Checksum(expectedChecksumAlgorithm, expectedChecksumValue);

        final TransferChannel expectedUploadChannel = null;
        final TransferChannel expectedDownloadChannel = null;

        final var actualException = assertThrows(
                ValidationException.class,
                () -> File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedUploadChannel,
                        expectedDownloadChannel));

        final var actualExceptionMessage = actualException.getMessage();
        final var actualErrors = actualException.getErrors();
        final var actualErrorsCount = actualException.getErrors().size();

        assertEquals(actualExceptionMessage, expectedExceptionMessage);
        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedErrorsCount, actualErrorsCount);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());

    }

    @Test
    void givenNullId_whenConstructorCall_thenShoulThrowsNullPointerException() {

        final FileId expectedFileId = null;
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = new Checksum(expectedChecksumAlgorithm, expectedChecksumValue);

        final TransferChannel expectedUploadChannel = null;
        final TransferChannel expectedDownloadChannel = null;

        final var actualException = assertThrows(
                NullPointerException.class,
                () -> File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedUploadChannel,
                        expectedDownloadChannel));
        assertEquals("'id' should not be null", actualException.getMessage());

    }

    @Test
    void givenNullChecksum_whenValidate_thenHandlerShouldAppendError() {

        final var expectedExceptionMessage = "'File' validation failed";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "checksum cant be null";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = new FileId(expectedIdValue);
        final var expectedSize = new Size(2L);

        final Checksum expectedChecksum = null;

        final TransferChannel expectedUploadChannel = null;
        final TransferChannel expectedDownloadChannel = null;

        final var actualException = assertThrows(
                ValidationException.class,
                () -> File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedUploadChannel,
                        expectedDownloadChannel));

        final var actualExceptionMessage = actualException.getMessage();
        final var actualErrors = actualException.getErrors();
        final var actualErrorsCount = actualException.getErrors().size();

        assertEquals(actualExceptionMessage, expectedExceptionMessage);
        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedErrorsCount, actualErrorsCount);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());

    }

    @Test
    void givenValidFile_whenValidate_thenHandlerShouldNotAppendError() {
        final var expectedErrorsCount = 0;
        final var expectedHasErrors = false;

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = new FileId(expectedIdValue);
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = new Checksum(expectedChecksumAlgorithm, expectedChecksumValue);

        final TransferChannel expectedUploadChannel = null;
        final TransferChannel expectedDownloadChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                expectedSize,
                expectedChecksum,
                expectedUploadChannel,
                expectedDownloadChannel);

        final var handler = Notification.create();

        expectedFile.validate(handler);

        final var actualHasErrors = handler.hasErrors();
        final var actualErrors = handler.getErrors();

        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedHasErrors, actualHasErrors);

    }
}