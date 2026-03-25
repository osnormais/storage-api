package org.osnormais.storage.api.domain.file;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.exception.FileAlreadyPublishedException;
import org.osnormais.storage.api.domain.exception.FileUploadInProgressException;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.exception.TransferChannelAlreadyOpennedException;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.event.FilePublishFailedEvent;
import org.osnormais.storage.api.domain.file.event.FilePublishedEvent;
import org.osnormais.storage.api.domain.file.event.FileUploadTransferChannelCompletedEvent;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Publication;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.validation.handler.Notification;

class FileTest {

    @Nested
    class With {

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

            final Publication expectedPublication = null;
            final TransferChannel expectedUploadChannel = null;
            final TransferChannel expectedDownloadChannel = null;

            final var actualException = assertThrows(
                    ValidationException.class,
                    () -> File.with(
                            expectedFileId,
                            expectedSize,
                            expectedChecksum,
                            expectedPublication,
                            expectedUploadChannel,
                            expectedDownloadChannel,
                            null));

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

            final Publication expectedPublication = null;
            final TransferChannel expectedUploadChannel = null;
            final TransferChannel expectedDownloadChannel = null;

            final var actualException = assertThrows(
                    NullPointerException.class,
                    () -> File.with(
                            expectedFileId,
                            expectedSize,
                            expectedChecksum,
                            expectedPublication,
                            expectedUploadChannel,
                            expectedDownloadChannel,
                            null));
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

            final Publication expectedPublication = null;
            final TransferChannel expectedUploadChannel = null;
            final TransferChannel expectedDownloadChannel = null;

            final var actualException = assertThrows(
                    ValidationException.class,
                    () -> File.with(
                            expectedFileId,
                            expectedSize,
                            expectedChecksum,
                            expectedPublication,
                            expectedUploadChannel,
                            expectedDownloadChannel,
                            null));

            final var actualExceptionMessage = actualException.getMessage();
            final var actualErrors = actualException.getErrors();
            final var actualErrorsCount = actualException.getErrors().size();

            assertEquals(actualExceptionMessage, expectedExceptionMessage);
            assertEquals(expectedErrorsCount, actualErrors.size());
            assertEquals(expectedErrorsCount, actualErrorsCount);
            assertEquals(expectedErrorMessage, actualErrors.get(0).message());

        }

    }

    @Nested
    class Validate {

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

            final Publication expectedPublication = null;
            final TransferChannel expectedUploadChannel = null;
            final TransferChannel expectedDownloadChannel = null;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    expectedUploadChannel,
                    expectedDownloadChannel,
                    null);

            final var handler = Notification.create();

            expectedFile.validate(handler);

            final var actualHasErrors = handler.hasErrors();
            final var actualErrors = handler.getErrors();

            assertEquals(expectedErrorsCount, actualErrors.size());
            assertEquals(expectedHasErrors, actualHasErrors);

        }

    }

    @Nested
    class Create {

        @Test
        void givenValidArguments_whenCallsCreate_thenShouldCreateFile() {

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final var expectedPublication = Optional.<Publication>empty();
            final var expectedUploadChannel = Optional.<TransferChannel>empty();
            final var expectedDownloadChannel = Optional.<TransferChannel>empty();

            final var actualFile = assertDoesNotThrow(
                    () -> File.create(expectedFileId, expectedSize, expectedChecksum));

            assertEquals(expectedIdValue, actualFile.getId().getValue());
            assertEquals(expectedFileId, actualFile.getId());
            assertEquals(expectedSize, actualFile.getSize());
            assertEquals(expectedChecksum, actualFile.getChecksum());
            assertEquals(expectedPublication, actualFile.getPublication());
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

    @Nested
    class OpenUploadChannel {

        @Test
        void givenValidTransferChannel_whenCallsOpenUploadChannel_thenShouldOpenChannel() {

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final var expectedThroughputLimit = ThroughputLimit.create(100L);
            final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L),
                    ParallelChunkLimit.of(2));

            final Publication expectedPublication = null;

            final TransferChannel expectedDownloadChannel = null;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    null,
                    expectedDownloadChannel,
                    null);

            assertTrue(expectedFile.getUploadChannel().isEmpty());

            assertDoesNotThrow(() -> expectedFile
                    .openUploadChannel(expectedThroughputLimit, expectedChunkSpecification));

            assertTrue(expectedFile.getUploadChannel().isPresent());
            assertEquals(expectedThroughputLimit, expectedFile.getUploadChannel().get().getThroughputLimit());
            assertEquals(expectedChunkSpecification, expectedFile.getUploadChannel().get().getChunkSpecification());

        }

        @Test
        void givenNullTransferChannel_whenCallsOpenUploadChannel_thenShouldThrowsInvalidArgumentException() {

            final var expectedExceptionMessage = "Invalid argument provided.";
            final var expectedErrorsCount = 1;
            final var expectedErrorMessage = "'throughputLimit' should not be null";

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final Publication expectedPublication = null;

            final ThroughputLimit expectedUploadChannelThroughputLimit = null;
            final ChunkSpecification expectedUploadChannelChunkSpecification = null;
            final TransferChannel expectedUploadChannel = null;
            final TransferChannel expectedDownloadChannel = null;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    expectedUploadChannel,
                    expectedDownloadChannel,
                    null);

            assertTrue(expectedFile.getUploadChannel().isEmpty());

            final var actualException = assertThrows(
                    InvalidArgumentException.class,
                    () -> expectedFile.openUploadChannel(
                            expectedUploadChannelThroughputLimit,
                            expectedUploadChannelChunkSpecification));

            final var actualExceptionMessage = actualException.getMessage();
            final var actualErrors = actualException.getErrors();
            final var actualErrorsCount = actualException.getErrors().size();

            assertEquals(actualExceptionMessage, expectedExceptionMessage);
            assertEquals(expectedErrorsCount, actualErrors.size());
            assertEquals(expectedErrorsCount, actualErrorsCount);
            assertEquals(expectedErrorMessage, actualErrors.get(0).message());

        }

        @Test
        void givenValidTransferChannel_whenCallsOpenUploadChannelWithAlreadyOpenChannel_thenShouldThrowsUploadTransferChannelAlreadyOpennedException() {

            final var expectedExceptionMessage = "Transfer channel already open";
            final var expectedErrorsCount = 1;
            final var expectedErrorMessage = "Transfer channel already open, please close the current channel before opening a new one";

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final Publication expectedPublication = null;
            final var expectedThroughputLimit = ThroughputLimit.create(100L);
            final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L),
                    ParallelChunkLimit.of(2));

            final var expectedUploadChannel = TransferChannel.create(
                    expectedThroughputLimit,
                    expectedChunkSpecification);

            final TransferChannel expectedDownloadChannel = null;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    expectedUploadChannel,
                    expectedDownloadChannel,
                    null);

            assertTrue(expectedFile.getUploadChannel().isPresent());

            final var actualException = assertThrows(
                    TransferChannelAlreadyOpennedException.class,
                    () -> expectedFile.openUploadChannel(expectedThroughputLimit, expectedChunkSpecification));

            final var actualExceptionMessage = actualException.getMessage();
            final var actualErrors = actualException.getErrors();
            final var actualErrorsCount = actualException.getErrors().size();

            assertEquals(actualExceptionMessage, expectedExceptionMessage);
            assertEquals(expectedErrorsCount, actualErrors.size());
            assertEquals(expectedErrorsCount, actualErrorsCount);
            assertEquals(expectedErrorMessage, actualErrors.get(0).message());

        }

        @Test
        void givenValidTransferChannel_whenCallsOpenUploadChannelWithAlreadyPublishedFile_thenShouldThrowsFileAlreadyPublishedException() {

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedExceptionMessage = "File [%s], already published"
                    .formatted(expectedIdValue.toString());
            final var expectedErrorsCount = 1;
            final var expectedErrorMessage = expectedExceptionMessage;

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final var expectedPublication = Publication.ok();
            final var expectedThroughputLimit = ThroughputLimit.create(100L);
            final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L),
                    ParallelChunkLimit.of(2));

            final var expectedUploadChannel = TransferChannel.create(
                    expectedThroughputLimit,
                    expectedChunkSpecification);

            final TransferChannel expectedDownloadChannel = null;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    expectedUploadChannel,
                    expectedDownloadChannel,
                    null);

            assertTrue(expectedFile.getUploadChannel().isPresent());

            final var actualException = assertThrows(
                    FileAlreadyPublishedException.class,
                    () -> expectedFile.openUploadChannel(expectedThroughputLimit, expectedChunkSpecification));

            final var actualExceptionMessage = actualException.getMessage();
            final var actualErrors = actualException.getErrors();

            assertEquals(actualExceptionMessage, expectedExceptionMessage);
            assertEquals(expectedErrorsCount, actualErrors.size());
            assertEquals(expectedErrorMessage, actualErrors.get(0).message());

        }

    }

    @Nested
    class CompleteUploadChannel {

        @Test
        void givenAnEmptyUploadChannel_whenCallsCompleteUploadChannel_thenShouldNotCompleteChannel() {

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final Publication expectedPublication = null;
            final TransferChannel expectedUploadChannel = null;
            final TransferChannel expectedDownloadChannel = null;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    expectedUploadChannel,
                    expectedDownloadChannel,
                    null);

            assertTrue(expectedFile.getUploadChannel().isEmpty());

            final var actualFile = assertDoesNotThrow(() -> expectedFile.completeUploadChannel());

            assertTrue(actualFile.getUploadChannel().isEmpty());
            assertTrue(actualFile.nextEvent().isEmpty());

        }

        @Test
        void givenAnPopulatedUploadChannel_whenCallsCompleteUploadChannel_thenShouldCompleteChannel() {

            final var expectedIdValue = UUID.randomUUID();
            final var expectedFileId = FileId.of(expectedIdValue);
            final var expectedSize = new Size(2L);

            final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
            final var expectedChecksumValue = "123";
            final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

            final var expectedThroughputLimit = ThroughputLimit.create(100L);
            final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L),
                    ParallelChunkLimit.of(2));

            final Publication expectedPublication = null;
            final var expectedUploadChannel = TransferChannel.with(
                    TransferChannelId.unique(),
                    TransferChannelStatus.OPENED,
                    expectedThroughputLimit,
                    expectedChunkSpecification);
            final TransferChannel expectedDownloadChannel = null;

            final Boolean expectedUploadChannelIsOpen = false;

            final var expectedFile = File.with(
                    expectedFileId,
                    expectedSize,
                    expectedChecksum,
                    expectedPublication,
                    expectedUploadChannel,
                    expectedDownloadChannel,
                    null);

            assertTrue(expectedFile.getUploadChannel().isPresent());

            final var actualFile = assertDoesNotThrow(() -> expectedFile.completeUploadChannel());

            final var actualEvent = actualFile.nextEvent();

            assertTrue(actualFile.getUploadChannel().isPresent());
            assertEquals(expectedUploadChannelIsOpen, actualFile.getUploadChannel().get().isOpen());
            assertTrue(actualEvent.isPresent());
            assertTrue(actualEvent.get() instanceof FileUploadTransferChannelCompletedEvent);

        }

    }

    @Nested
    class FinalizePublication {

        @Nested
        class Publicate {

            @Test
            void givenAValidUnpublishedFile_whenCallsPublicate_thenShouldCompletePublication() {

                final var expectedIdValue = UUID.randomUUID();
                final var expectedFileId = FileId.of(expectedIdValue);
                final var expectedSize = new Size(2L);

                final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
                final var expectedChecksumValue = "123";
                final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm,
                        expectedChecksumValue);

                final var expectedPublicationStatus = Publication.Status.OK;
                final TransferChannel expectedUploadChannel = null;
                final TransferChannel expectedDownloadChannel = null;

                final var expectedFile = File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        null,
                        expectedUploadChannel,
                        expectedDownloadChannel,
                        null);

                final var actualFile = assertDoesNotThrow(
                        () -> expectedFile.publicate(() -> expectedChecksum));

                final var actualEvent = actualFile.nextEvent();

                assertTrue(actualEvent.isPresent());
                assertTrue(actualEvent.get() instanceof FilePublishedEvent);

                assertEquals(expectedPublicationStatus, actualFile.getPublication().get().status());

            }

            @Test
            void givenAValidPublishedFile_whenCallsPublicate_thenShouldThrowsFileAlreadyPublishedException() {

                final var expectedIdValue = UUID.randomUUID();
                final var expectedFileId = FileId.of(expectedIdValue);
                final var expectedSize = new Size(2L);

                final var expectedExceptionMessage = "File [%s], already published"
                        .formatted(expectedIdValue.toString());
                final var expectedErrorsCount = 1;
                final var expectedErrorMessage = expectedExceptionMessage;

                final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
                final var expectedChecksumValue = "123";
                final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm,
                        expectedChecksumValue);

                final var expectedPublication = Publication.ok();
                final TransferChannel expectedUploadChannel = null;
                final TransferChannel expectedDownloadChannel = null;

                final var expectedFile = File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedPublication,
                        expectedUploadChannel,
                        expectedDownloadChannel,
                        null);

                final var actualException = assertThrows(
                        FileAlreadyPublishedException.class,
                        () -> expectedFile.publicate(() -> expectedChecksum));

                assertEquals(expectedExceptionMessage, actualException.getMessage());
                assertEquals(expectedErrorsCount, actualException.getErrors().size());
                assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            }

            @Test
            void givenAValidUnpublishedFile_whenCallsPublicateWithExistingUploadChannel_thenShouldThrowsFileUploadInProgressException() {

                final var expectedIdValue = UUID.randomUUID();
                final var expectedFileId = FileId.of(expectedIdValue);
                final var expectedSize = new Size(2L);

                final var expectedExceptionMessage = "Upload transfer channel openned for this file [%s], upload in progress"
                        .formatted(expectedIdValue.toString());
                final var expectedErrorsCount = 1;
                final var expectedErrorMessage = expectedExceptionMessage
                        + ", please close the current channel before publishing the file";

                final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
                final var expectedChecksumValue = "123";
                final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm,
                        expectedChecksumValue);

                final var expectedThroughputLimit = ThroughputLimit.create(100L);
                final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L),
                        ParallelChunkLimit.of(2));

                final Publication expectedPublication = null;
                final var expectedUploadChannel = TransferChannel.create(
                        expectedThroughputLimit,
                        expectedChunkSpecification);
                final TransferChannel expectedDownloadChannel = null;

                final var expectedFile = File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedPublication,
                        expectedUploadChannel,
                        expectedDownloadChannel,
                        null);

                final var actualException = assertThrows(
                        FileUploadInProgressException.class,
                        () -> expectedFile.publicate(() -> expectedChecksum));

                final var actualExceptionMessage = actualException.getMessage();
                final var actualErrors = actualException.getErrors();
                final var actualErrorsCount = actualException.getErrors().size();

                assertEquals(actualExceptionMessage, expectedExceptionMessage);
                assertEquals(expectedErrorsCount, actualErrors.size());
                assertEquals(expectedErrorsCount, actualErrorsCount);
                assertEquals(expectedErrorMessage, actualErrors.get(0).message());

            }

        }

        @Nested
        class FailPublication {

            @Test
            void givenAValidUnpublishedFile_whenCallsFinalizePublicationWithInvalidChecksum_thenShouldFailPublication() {

                final var expectedIdValue = UUID.randomUUID();
                final var expectedFileId = FileId.of(expectedIdValue);
                final var expectedSize = new Size(2L);

                final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
                final var expectedChecksumValue = "123";
                final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm,
                        expectedChecksumValue);

                final var expectedPublicationStatus = Publication.Status.ERROR;
                final var expectedPublicationError = Publication.Error.of(
                        "File integrity compromised during finalization. Expected checksum: Checksum[algorithm=CRC_32, value=123], actual checksum: Checksum[algorithm=CRC_32, value=abc123]");
                final TransferChannel expectedUploadChannel = null;
                final TransferChannel expectedDownloadChannel = null;

                final var expectedFile = File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        null,
                        expectedUploadChannel,
                        expectedDownloadChannel,
                        null);

                final var actualFile = assertDoesNotThrow(() -> expectedFile
                        .publicate(() -> Checksum.of(expectedChecksumAlgorithm, "abc123")));

                assertEquals(expectedPublicationStatus, actualFile.getPublication().get().status());
                assertEquals(expectedPublicationError, actualFile.getPublication().get().error().get());

                final var actualEvent = actualFile.nextEvent();
                assertTrue(actualEvent.isPresent());
                assertTrue(actualEvent.get() instanceof FilePublishFailedEvent);

                assertEquals(expectedPublicationStatus, actualFile.getPublication().get().status());

            }

            @Test
            void givenAValidUnpublishedFile_whenCallsFinalizePublicationWithExistingUploadChannel_thenShouldThrowsFileUploadInProgressException() {

                final var expectedIdValue = UUID.randomUUID();
                final var expectedFileId = FileId.of(expectedIdValue);
                final var expectedSize = new Size(2L);

                final var expectedExceptionMessage = "Upload transfer channel openned for this file [%s], upload in progress"
                        .formatted(expectedIdValue.toString());
                final var expectedErrorsCount = 1;
                final var expectedErrorMessage = expectedExceptionMessage
                        + ", please close the current channel before publishing the file";

                final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
                final var expectedChecksumValue = "123";
                final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm,
                        expectedChecksumValue);

                final var expectedThroughputLimit = ThroughputLimit.create(100L);
                final var expectedChunkSpecification = ChunkSpecification.create(Size.of(1024L),
                        ParallelChunkLimit.of(2));

                final Publication expectedPublication = null;
                final var expectedUploadChannel = TransferChannel.create(
                        expectedThroughputLimit,
                        expectedChunkSpecification);
                final TransferChannel expectedDownloadChannel = null;

                final var expectedFile = File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedPublication,
                        expectedUploadChannel,
                        expectedDownloadChannel,
                        null);

                final var actualException = assertThrows(
                        FileUploadInProgressException.class,
                        () -> expectedFile.publicate(() -> expectedChecksum));

                final var actualExceptionMessage = actualException.getMessage();
                final var actualErrors = actualException.getErrors();
                final var actualErrorsCount = actualException.getErrors().size();

                assertEquals(actualExceptionMessage, expectedExceptionMessage);
                assertEquals(expectedErrorsCount, actualErrors.size());
                assertEquals(expectedErrorsCount, actualErrorsCount);
                assertEquals(expectedErrorMessage, actualErrors.get(0).message());

            }

            @Test
            void givenAValidPublishedFile_whenCallsFinalizePublicationWithExistingUploadChannel_thenShouldThrowsFileAlreadyPublishedException() {

                final var expectedIdValue = UUID.randomUUID();
                final var expectedFileId = FileId.of(expectedIdValue);
                final var expectedSize = new Size(2L);

                final var expectedExceptionMessage = "File [%s], already published"
                        .formatted(expectedIdValue.toString());
                final var expectedErrorsCount = 1;
                final var expectedErrorMessage = expectedExceptionMessage;

                final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
                final var expectedChecksumValue = "123";
                final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm,
                        expectedChecksumValue);

                final Publication expectedPublication = Publication.ok();
                final TransferChannel expectedUploadChannel = null;
                final TransferChannel expectedDownloadChannel = null;

                final var expectedFile = File.with(
                        expectedFileId,
                        expectedSize,
                        expectedChecksum,
                        expectedPublication,
                        expectedUploadChannel,
                        expectedDownloadChannel,
                        null);

                final var actualException = assertThrows(
                        FileAlreadyPublishedException.class,
                        () -> expectedFile.publicate(() -> Checksum
                                .of(expectedChecksumAlgorithm, "abc123")));

                final var actualExceptionMessage = actualException.getMessage();
                final var actualErrors = actualException.getErrors();
                final var actualErrorsCount = actualException.getErrors().size();

                assertEquals(actualExceptionMessage, expectedExceptionMessage);
                assertEquals(expectedErrorsCount, actualErrors.size());
                assertEquals(expectedErrorsCount, actualErrorsCount);
                assertEquals(expectedErrorMessage, actualErrors.get(0).message());

            }

        }

    }

}