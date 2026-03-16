package org.osnormais.storage.api.application.usecase.file.chunk.upload;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osnormais.storage.api.application.exception.ChunkIntegrityViolationException;
import org.osnormais.storage.api.application.exception.ConcurrentChunkLimitExceededException;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.exception.TrasnferChannelNotAvailableException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;

@ExtendWith(MockitoExtension.class)
public class DefaultUploadFileChunkUseCaseTest {

    @InjectMocks
    DefaultUploadFileChunkUseCase useCase;

    @Mock
    FileQueryGateway fileQueryGateway;

    @Mock
    ConcurrencyTracker concurrencyTracker;

    @Mock
    ChunkWriter chunkWriter;

    @Test
    void givenAnValidInput_whenCallsExecute_thenShouldUploadChunk() {

        final var expectedChunkIndex = 0L;
        final var expectedInputStream = InputStream.nullInputStream();
        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "checksum";

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileSizeInBytesValue = 1024L;
        final var expectedFileChecksumAlgorithm = expectedChecksumAlgorithm;
        final var expectedFileChecksumValue = expectedChecksumValue;

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSize = Size.of(expectedFileSizeInBytesValue);
        final var expectedFileChecksum = Checksum.of(expectedFileChecksumAlgorithm, expectedFileChecksumValue);

        final var expectedThroughputLimitBytesPerSecond = 1024L;
        final var expectedThroughputLimit = ThroughputLimit.create(expectedThroughputLimitBytesPerSecond);

        final var expectedChunkSpecificationSizeValue = 100L;
        final var expectedChunkSpecificationSize = Size.of(expectedChunkSpecificationSizeValue);

        final var expectedParallelChunkLimitValue = 2;
        final var expectedParallelChunkLimit = ParallelChunkLimit.of(expectedParallelChunkLimitValue);

        final var expectedChunkSpecification = ChunkSpecification.create(
                expectedChunkSpecificationSize,
                expectedParallelChunkLimit);

        final var expectedUploadTrasnferChannel = TransferChannel.create(
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                expectedUploadTrasnferChannel,
                null,
                null);

        final var expectedConcurrencyTrackerCount = 0;

        final var chunkSize = expectedUploadTrasnferChannel
                .chunkSpecification()
                .effectiveChunkSize(
                        expectedFileSize,
                        expectedChunkIndex);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(concurrencyTracker.getCurrentCount(expectedFileId))
                .thenReturn(expectedConcurrencyTrackerCount);

        doNothing()
                .when(concurrencyTracker)
                .increment(expectedFileId);

        when(chunkWriter
                .writeChunk(
                        expectedFileId,
                        expectedChunkIndex,
                        expectedChunkSpecificationSize,
                        expectedThroughputLimit,
                        expectedFileChecksumAlgorithm,
                        expectedInputStream))
                .thenReturn(expectedFileChecksum);

        doNothing()
                .when(concurrencyTracker)
                .decrement(expectedFileId);

        final var input = new UploadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex,
                expectedInputStream,
                expectedChecksumAlgorithm,
                expectedChecksumValue);

        assertDoesNotThrow(() -> useCase.execute(input));

        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(concurrencyTracker, times(1)).getCurrentCount(any());
        verify(concurrencyTracker, times(1)).getCurrentCount(expectedFileId);
        verify(concurrencyTracker, times(1)).increment(any());
        verify(concurrencyTracker, times(1)).increment(expectedFileId);
        verify(chunkWriter, times(1)).writeChunk(any(), any(), any(), any(), any(), any());
        verify(chunkWriter, times(1)).writeChunk(
                expectedFileId,
                expectedChunkIndex,
                chunkSize,
                expectedThroughputLimit,
                expectedChecksumAlgorithm,
                expectedInputStream);
        verify(concurrencyTracker, times(1)).decrement(any());
        verify(concurrencyTracker, times(1)).decrement(expectedFileId);

    }

    @Test
    void givenAValidArguments_whenMaxParallelChunksExceeded_thenShouldThrowsConcurrentChunkLimitExceededException() {

        final var expectedChunkIndex = 0L;
        final var expectedInputStream = InputStream.nullInputStream();
        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "checksum";

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileSizeInBytesValue = 1024L;
        final var expectedFileChecksumAlgorithm = expectedChecksumAlgorithm;
        final var expectedFileChecksumValue = expectedChecksumValue;

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSize = Size.of(expectedFileSizeInBytesValue);
        final var expectedFileChecksum = Checksum.of(expectedFileChecksumAlgorithm, expectedFileChecksumValue);

        final var expectedThroughputLimitBytesPerSecond = 1024L;
        final var expectedThroughputLimit = ThroughputLimit.create(expectedThroughputLimitBytesPerSecond);

        final var expectedChunkSpecificationSizeValue = 100L;
        final var expectedChunkSpecificationSize = Size.of(expectedChunkSpecificationSizeValue);

        final var expectedParallelChunkLimitValue = 2;
        final var expectedParallelChunkLimit = ParallelChunkLimit.of(expectedParallelChunkLimitValue);

        final var expectedChunkSpecification = ChunkSpecification.create(
                expectedChunkSpecificationSize,
                expectedParallelChunkLimit);

        final var expectedUploadTrasnferChannel = TransferChannel.create(
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                expectedUploadTrasnferChannel,
                null,
                null);

        final var expectedConcurrencyTrackerCount = 2;

        final var expectedExceptionMessage = "Maximum parallel chunk reached for the file.";
        final var expectedExceptionErrrosCount = 1;
        final var expectedExceptionErrrorMessage0 = "Maximum parallel chunk of "
                + expectedParallelChunkLimitValue
                + " exceeded for the file.";

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(concurrencyTracker.getCurrentCount(expectedFileId))
                .thenReturn(expectedConcurrencyTrackerCount);

        final var input = new UploadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex,
                expectedInputStream,
                expectedChecksumAlgorithm,
                expectedChecksumValue);

        final var actualException = assertThrows(
                ConcurrentChunkLimitExceededException.class,
                () -> useCase.execute(input));

        assertEquals(actualException.getMessage(), expectedExceptionMessage);
        assertEquals(actualException.getErrors().size(), expectedExceptionErrrosCount);
        assertEquals(actualException.getErrors().get(0).message(), expectedExceptionErrrorMessage0);

        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(concurrencyTracker, times(1)).getCurrentCount(any());
        verify(concurrencyTracker, times(1)).getCurrentCount(expectedFileId);
        verify(concurrencyTracker, times(0)).increment(any());
        verify(chunkWriter, times(0)).writeChunk(any(), any(), any(), any(), any(), any());
        verify(concurrencyTracker, times(0)).decrement(any());

    }

    @Test
    void givenValidArguments_whenFileDoesntHaveUploadTransferChannel_thenShouldTrasnferChannelNotAvailableException() {

        final var expectedChunkIndex = 0L;
        final var expectedInputStream = InputStream.nullInputStream();
        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "checksum";

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileSizeInBytesValue = 1024L;
        final var expectedFileChecksumAlgorithm = expectedChecksumAlgorithm;
        final var expectedFileChecksumValue = expectedChecksumValue;

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSize = Size.of(expectedFileSizeInBytesValue);
        final var expectedFileChecksum = Checksum.of(expectedFileChecksumAlgorithm, expectedFileChecksumValue);

        final TransferChannel expectedUploadTrasnferChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                expectedUploadTrasnferChannel,
                null,
                null);

        final var expectedExceptionMessage = "Trasnfer channel is not available.";
        final var expectedExceptionErrrosCount = 1;
        final var expectedExceptionErrrorMessage0 = "["
                + "upload"
                + "]"
                + "Trasnfer channel of File=["
                + expectedFileId.getStringValue()
                + "] is not available";

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        final var input = new UploadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex,
                expectedInputStream,
                expectedChecksumAlgorithm,
                expectedChecksumValue);

        final var actualException = assertThrows(
                TrasnferChannelNotAvailableException.class,
                () -> useCase.execute(input));

        assertEquals(actualException.getMessage(), expectedExceptionMessage);
        assertEquals(actualException.getErrors().size(), expectedExceptionErrrosCount);
        assertEquals(actualException.getErrors().get(0).message(), expectedExceptionErrrorMessage0);

        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(concurrencyTracker, times(0)).getCurrentCount(any());
        verify(concurrencyTracker, times(0)).increment(any());
        verify(chunkWriter, times(0)).writeChunk(any(), any(), any(), any(), any(), any());
        verify(concurrencyTracker, times(0)).decrement(any());

    }

    @Test
    void givenAnNonExistentFileId_whenCallsExecute_thenShouldThrowsNotFoundExcpetion() {

        final var expectedChunkIndex = 0L;
        final var expectedInputStream = InputStream.nullInputStream();
        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "checksum";

        final var expectedFileIdValue = UUID.randomUUID();

        final var expectedFileId = FileId.of(expectedFileIdValue);

        final var expectedExceptionMessage = "[%s] not found".formatted(File.class.getSimpleName());
        final var expectedExceptionErrrosCount = 1;
        final var expectedExceptionErrrorMessage0 = "[%s] with id [%s] not found".formatted(File.class.getSimpleName(),
                expectedFileId.getStringValue());

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.empty());

        final var input = new UploadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex,
                expectedInputStream,
                expectedChecksumAlgorithm,
                expectedChecksumValue);

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(input));

        assertEquals(actualException.getMessage(), expectedExceptionMessage);
        assertEquals(actualException.getErrors().size(), expectedExceptionErrrosCount);
        assertEquals(actualException.getErrors().get(0).message(), expectedExceptionErrrorMessage0);

        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(concurrencyTracker, times(0)).getCurrentCount(any());
        verify(concurrencyTracker, times(0)).increment(any());
        verify(chunkWriter, times(0)).writeChunk(any(), any(), any(), any(), any(), any());
        verify(concurrencyTracker, times(0)).decrement(any());

    }

    @Test
    void givenAnBrokenChecksum_whenCallsExecute_thenShouldThrowsChunkIntegrityViolationException() {

        final var expectedChunkIndex = 0L;
        final var expectedInputStream = InputStream.nullInputStream();
        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "wrong_checksum";
        final var expectedChecksum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileSizeInBytesValue = 1024L;
        final var expectedFileChecksumAlgorithm = expectedChecksumAlgorithm;
        final var expectedFileChecksumValue = "file_correct_checksum";

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSize = Size.of(expectedFileSizeInBytesValue);
        final var expectedFileChecksum = Checksum.of(expectedFileChecksumAlgorithm, expectedFileChecksumValue);

        final var expectedChunkChecksumAlgorithm = expectedChecksumAlgorithm;
        final var expectedChunkChecksumValue = "chunk_correct_checksum";
        final var expectedChunkChecksum = Checksum.of(expectedChunkChecksumAlgorithm, expectedChunkChecksumValue);

        final var expectedThroughputLimitBytesPerSecond = 1024L;
        final var expectedThroughputLimit = ThroughputLimit.create(expectedThroughputLimitBytesPerSecond);

        final var expectedChunkSpecificationSizeValue = 100L;
        final var expectedChunkSpecificationSize = Size.of(expectedChunkSpecificationSizeValue);

        final var expectedParallelChunkLimitValue = 2;
        final var expectedParallelChunkLimit = ParallelChunkLimit.of(expectedParallelChunkLimitValue);

        final var expectedChunkSpecification = ChunkSpecification.create(
                expectedChunkSpecificationSize,
                expectedParallelChunkLimit);

        final var expectedUploadTrasnferChannel = TransferChannel.create(
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                expectedUploadTrasnferChannel,
                null,
                null);

        final var expectedConcurrencyTrackerCount = 0;

        final var chunkSize = expectedUploadTrasnferChannel
                .chunkSpecification()
                .effectiveChunkSize(
                        expectedFileSize,
                        expectedChunkIndex);

        final var expectedExceptionMessage = "Chunk integrity violation.";
        final var expectedExceptionErrorsCount = 1;
        final var expectedExceptionErrorMessage0 = "Chunk integrity violation: expected "
                + expectedChecksum
                + ", but got "
                + expectedChunkChecksum;

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(concurrencyTracker.getCurrentCount(expectedFileId))
                .thenReturn(expectedConcurrencyTrackerCount);

        doNothing()
                .when(concurrencyTracker)
                .increment(expectedFileId);

        when(chunkWriter
                .writeChunk(
                        expectedFileId,
                        expectedChunkIndex,
                        expectedChunkSpecificationSize,
                        expectedThroughputLimit,
                        expectedFileChecksumAlgorithm,
                        expectedInputStream))
                .thenReturn(expectedChunkChecksum);

        doNothing()
                .when(concurrencyTracker)
                .decrement(expectedFileId);

        final var input = new UploadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex,
                expectedInputStream,
                expectedChecksumAlgorithm,
                expectedChecksumValue);

        final var actualException = assertThrows(ChunkIntegrityViolationException.class, () -> useCase.execute(input));

        assertEquals(actualException.getMessage(), expectedExceptionMessage);
        assertEquals(actualException.getErrors().size(), expectedExceptionErrorsCount);
        assertEquals(actualException.getErrors().get(0).message(), expectedExceptionErrorMessage0);

        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(concurrencyTracker, times(1)).getCurrentCount(any());
        verify(concurrencyTracker, times(1)).getCurrentCount(expectedFileId);
        verify(concurrencyTracker, times(1)).increment(any());
        verify(concurrencyTracker, times(1)).increment(expectedFileId);
        verify(chunkWriter, times(1)).writeChunk(any(), any(), any(), any(), any(), any());
        verify(chunkWriter, times(1)).writeChunk(
                expectedFileId,
                expectedChunkIndex,
                chunkSize,
                expectedThroughputLimit,
                expectedChecksumAlgorithm,
                expectedInputStream);
        verify(concurrencyTracker, times(1)).decrement(any());
        verify(concurrencyTracker, times(1)).decrement(expectedFileId);

    }

}
