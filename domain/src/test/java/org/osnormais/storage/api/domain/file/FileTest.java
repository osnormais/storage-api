package org.osnormais.storage.api.domain.file;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.exception.UploadTransferChannelAlreadyOpennedException;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class FileTest {

    @Test
    void givenNegativeSize_whenInstantiateUsingWith_thenShouldThrowsValidationException() {

        final var expectedExceptionMessage = "'File' validation failed";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "bytes must be greater than 0";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
        final var expectedSize = new Size(-2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

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
    void givenNullId_whenInstantiateUsingWith_thenShouldThrowsNullPointerException() {

        final FileId expectedFileId = null;
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

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
    void givenNullChecksum_whenInstantiateUsingWith_thenShouldThrowsValidationException() {

        final var expectedExceptionMessage = "'File' validation failed";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "checksum cant be null";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
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
        final var expectedFileId = FileId.of(expectedIdValue);
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

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

    @Test
    void givenValidTransferChannel_whenCallsOpenUploadChannel_thenShouldOpenChannel() {

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedThroughputLimit = ThroughputLimit.create(100L);
        final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L), ParallelChunkLimit.of(2));

        final var expectedUploadChannel = TransferChannel.create(
                expectedThroughputLimit,
                expectedChunkSpecification);

        final TransferChannel expectedDownloadChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                expectedSize,
                expectedChecksum,
                null,
                expectedDownloadChannel);

        assertTrue(expectedFile.getUploadChannel().isEmpty());

        assertDoesNotThrow(() -> expectedFile.openUploadChannel(expectedUploadChannel));

        assertTrue(expectedFile.getUploadChannel().isPresent());
        assertEquals(expectedUploadChannel, expectedFile.getUploadChannel().get());

    }

    @Test
    void givenNullTransferChannel_whenCallsOpenUploadChannel_thenShouldThrowsInvalidArgumentException() {

        final var expectedExceptionMessage = "Invalid argument provided.";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'transferChannel' should not be null";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final TransferChannel expectedUploadChannel = null;
        final TransferChannel expectedDownloadChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                expectedSize,
                expectedChecksum,
                expectedUploadChannel,
                expectedDownloadChannel);

        assertTrue(expectedFile.getUploadChannel().isEmpty());

        final var actualException = assertThrows(
                InvalidArgumentException.class,
                () -> expectedFile.openUploadChannel(expectedUploadChannel));

        final var actualExceptionMessage = actualException.getMessage();
        final var actualErrors = actualException.getErrors();
        final var actualErrorsCount = actualException.getErrors().size();

        assertEquals(actualExceptionMessage, expectedExceptionMessage);
        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedErrorsCount, actualErrorsCount);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());

    }

    @Test
    void givenValidTransferChannel_whenCallsOpenUploadChannelWithAlreadyOpenChannel_thenShouldOpenChannel() {

        final var expectedExceptionMessage = "Upload transfer channel already open";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "Upload transfer channel already open, please close the current channel before opening a new one";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedThroughputLimit = ThroughputLimit.create(100L);
        final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L), ParallelChunkLimit.of(2));

        final var expectedUploadChannel = TransferChannel.create(
                expectedThroughputLimit,
                expectedChunkSpecification);

        final TransferChannel expectedDownloadChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                expectedSize,
                expectedChecksum,
                expectedUploadChannel,
                expectedDownloadChannel);

        assertTrue(expectedFile.getUploadChannel().isPresent());

        final var actualException = assertThrows(
                UploadTransferChannelAlreadyOpennedException.class,
                () -> expectedFile.openUploadChannel(expectedUploadChannel));

        final var actualExceptionMessage = actualException.getMessage();
        final var actualErrors = actualException.getErrors();
        final var actualErrorsCount = actualException.getErrors().size();

        assertEquals(actualExceptionMessage, expectedExceptionMessage);
        assertEquals(expectedErrorsCount, actualErrors.size());
        assertEquals(expectedErrorsCount, actualErrorsCount);
        assertEquals(expectedErrorMessage, actualErrors.get(0).message());

    }

    @Test
    void givenValidArguments_whenCallsCreate_thenShouldCreateFile() {

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
        final var expectedSize = new Size(2L);

        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "123";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedUploadChannel = Optional.<TransferChannel>empty();
        final var expectedDownloadChannel = Optional.<TransferChannel>empty();

        final var actualFile = assertDoesNotThrow(() -> File.create(expectedFileId, expectedSize, expectedChecksum));

        assertEquals(expectedIdValue, actualFile.getId().getValue());
        assertEquals(expectedFileId, actualFile.getId());
        assertEquals(expectedSize, actualFile.getSize());
        assertEquals(expectedChecksum, actualFile.getChecksum());
        assertEquals(expectedUploadChannel, actualFile.getUploadChannel());
        assertEquals(expectedDownloadChannel, actualFile.getDownloadChannel());

    }

    @Test
    void givenNullArguments_whenCallsCreate_thenShouldThrowsValidationException() {

        final var expectedExceptionMessage = "'File' validation failed";

        final var expectedErrorCount = 2;
        final var expectedErrorMessage0 = "size cant be null";
        final var expectedErrorMessage1 = "checksum cant be null";

        final var expectedIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedIdValue);
        final Size expectedSize = null;
        final Checksum expectedChecksum = null;

        final var actualException = assertThrows(ValidationException.class,
                () -> File.create(expectedFileId, expectedSize, expectedChecksum));

        assertEquals(actualException.getMessage(), expectedExceptionMessage);
        assertEquals(actualException.getErrors().size(), expectedErrorCount);
        assertEquals(actualException.getErrors().get(0).message(), expectedErrorMessage0);
        assertEquals(actualException.getErrors().get(1).message(), expectedErrorMessage1);

    }

}