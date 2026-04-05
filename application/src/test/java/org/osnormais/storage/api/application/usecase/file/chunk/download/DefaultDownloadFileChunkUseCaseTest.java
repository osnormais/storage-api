package org.osnormais.storage.api.application.usecase.file.chunk.download;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osnormais.storage.api.application.exception.ConcurrentChunkLimitExceededException;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.exception.TransferChannelNotAvailableException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.domain.Identifier;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.TransferChannelId;
import org.osnormais.storage.api.domain.file.TransferChannelStatus;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

@ExtendWith(MockitoExtension.class)
public class DefaultDownloadFileChunkUseCaseTest {

    DefaultDownloadFileChunkUseCase useCase;

    FileQueryGateway fileQueryGateway;
    ConcurrencyTracker concurrencyTracker;
    ChunkReader chunkReader;

    ConcurrencyTracker.Port port;
    String[] tags = new String[] { "chunk-download" };

    @BeforeEach
    void setup() {

        port = Mockito.mock(ConcurrencyTracker.Port.class);

        fileQueryGateway = Mockito.mock(FileQueryGateway.class);
        concurrencyTracker = new ConcurrencyTracker(port, tags);
        chunkReader = Mockito.mock(ChunkReader.class);

        useCase = new DefaultDownloadFileChunkUseCase(
                fileQueryGateway,
                concurrencyTracker,
                chunkReader);

    }

    @Test
    void givenAValidInput_whenCallsExecute_thenShouldReturnChunkInputStream() {

        final var expectedChunkIndex = 0L;
        final var expectedInputStream = InputStream.nullInputStream();
        final var expectedChecksumAlgorithm = Checksum.Algorithm.CRC_32;
        final var expectedChecksumValue = "checksum";

        final var expectedChunkOffset = 0L;

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

        final var expectedTransferChannelIdValue = UUID.randomUUID();
        final var expectedTransferChannelId = TransferChannelId.of(expectedTransferChannelIdValue);
        final var expectedTransferChannelStatus = TransferChannelStatus.CLOSED;

        final var expectedUploadTransferChannel = TransferChannel.with(
                expectedTransferChannelId,
                expectedTransferChannelStatus,
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedDownloadTransferChannelIdValue = UUID.randomUUID();
        final var expectedDownloadTransferChannelId = TransferChannelId.of(expectedDownloadTransferChannelIdValue);
        final var expectedDownloadTransferChannelStatus = TransferChannelStatus.OPENED;

        final var expectedDownloadTransferChannel = TransferChannel.with(
                expectedDownloadTransferChannelId,
                expectedDownloadTransferChannelStatus,
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                null,
                expectedUploadTransferChannel,
                expectedDownloadTransferChannel,
                null);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(port.tryIncrement(expectedFileId, expectedParallelChunkLimitValue, tags))
                .thenReturn(true);

        when(chunkReader
                .readChunk(
                        expectedFileId,
                        expectedChunkOffset,
                        expectedChunkSpecificationSize,
                        expectedThroughputLimit))
                .thenReturn(expectedInputStream);

        doNothing()
                .when(port)
                .decrement(expectedFileId, tags);

        final var input = new DownloadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex);

        final var actualOutput = assertDoesNotThrow(() -> useCase.execute(input));

        assertEquals(expectedInputStream, actualOutput.data());

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());

        verify(port, times(1))
                .tryIncrement(expectedFileId, expectedParallelChunkLimitValue, tags);

        verify(port, times(0)).getCurrentCount(any(), any(), any());
        verify(port, times(0)).increment(any(), any());

        verify(port, times(1)).decrement(expectedFileId, tags);
        verify(port, times(1)).decrement(any(), any());

        verify(chunkReader, times(1)).readChunk(
                expectedFileId,
                expectedChunkOffset,
                expectedChunkSpecificationSize,
                expectedThroughputLimit);

        verify(chunkReader, times(1)).readChunk(
                any(),
                any(),
                any(),
                any());

    }

    @Test
    void givenAValidArguments_whenMaxParallelChunksExceeded_thenShouldThrowsConcurrentChunkLimitExceededException() {

        final var expectedChunkIndex = 0L;
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

        final var expectedTransferChannelIdValue = UUID.randomUUID();
        final var expectedTransferChannelId = TransferChannelId.of(expectedTransferChannelIdValue);
        final var expectedTransferChannelStatus = TransferChannelStatus.CLOSED;

        final var expectedUploadTransferChannel = TransferChannel.with(
                expectedTransferChannelId,
                expectedTransferChannelStatus,
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedDownloadTransferChannelIdValue = UUID.randomUUID();
        final var expectedDownloadTransferChannelId = TransferChannelId.of(expectedDownloadTransferChannelIdValue);
        final var expectedDownloadTransferChannelStatus = TransferChannelStatus.OPENED;

        final var expectedDownloadTransferChannel = TransferChannel.with(
                expectedDownloadTransferChannelId,
                expectedDownloadTransferChannelStatus,
                expectedThroughputLimit,
                expectedChunkSpecification);

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                null,
                expectedUploadTransferChannel,
                expectedDownloadTransferChannel,
                null);

        final var expectedExceptionMessage = "Maximum parallel chunk reached for the file.";
        final var expectedExceptionErrrosCount = 1;
        final var expectedExceptionErrrorMessage0 = "Maximum parallel chunk of "
                + expectedParallelChunkLimitValue
                + " exceeded for the file.";

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(port.tryIncrement(expectedFileId, expectedParallelChunkLimitValue, tags))
                .thenReturn(false);

        final var input = new DownloadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex);

        final var actualException = assertThrows(
                ConcurrentChunkLimitExceededException.class,
                () -> useCase.execute(input));

        assertEquals(actualException.getMessage(), expectedExceptionMessage);
        assertEquals(actualException.getErrors().size(), expectedExceptionErrrosCount);
        assertEquals(actualException.getErrors().get(0).message(), expectedExceptionErrrorMessage0);

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());

        verify(port, times(1))
                .tryIncrement(expectedFileId, expectedParallelChunkLimitValue, tags);
        verify(port, times(0)).getCurrentCount(any(), any());
        verify(port, times(0)).increment(any(), any());
        verify(port, times(0)).decrement(any(), any());
        verify(chunkReader, times(0)).readChunk(
                any(),
                any(),
                any(),
                any());

    }

    @Test
    void givenAValidInput_whenFileDoesntHaveDownloadTransferChannel_thenShouldReturnChunkInputStream() {

        final var expectedChunkIndex = 0L;
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

        final var expectedTransferChannelIdValue = UUID.randomUUID();
        final var expectedTransferChannelId = TransferChannelId.of(expectedTransferChannelIdValue);
        final var expectedTransferChannelStatus = TransferChannelStatus.CLOSED;

        final var expectedUploadTransferChannel = TransferChannel.with(
                expectedTransferChannelId,
                expectedTransferChannelStatus,
                expectedThroughputLimit,
                expectedChunkSpecification);

        final TransferChannel expectedDownloadTransferChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedFileChecksum,
                null,
                expectedUploadTransferChannel,
                expectedDownloadTransferChannel,
                null);

        final var expectedExceptionMessage = "Transfer channel is not available.";
        final var expectedErrrosCount = 1;
        final var expectedExceptionErrorMessage0 = "["
                + "download"
                + "]"
                + "Transfer channel of File=["
                + expectedFileIdValue.toString()
                + "] is not available".formatted("download", expectedFileIdValue);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        final var input = new DownloadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex);

        final var actualException = assertThrows(
                TransferChannelNotAvailableException.class,
                () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrrosCount, actualException.getErrors().size());
        assertEquals(expectedExceptionErrorMessage0, actualException.getErrors().get(0).message());

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());
        verify(port, times(0)).tryIncrement(any(Identifier.class), any(Integer.class), any(String[].class));
        verify(port, times(0)).getCurrentCount(any());
        verify(port, times(0)).increment(any());
        verify(port, times(0)).decrement(any());
        verify(chunkReader, times(0)).readChunk(
                any(),
                any(),
                any(),
                any());
    }

    @Test
    void givenAnInexistentFile_whenCallsExecute_thenShouldThrowsNotFoundException() {

        final var expectedChunkIndex = 0L;

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);

        final var expectedExceptionMessage = "[File] not found";
        final var expectedErrrosCount = 1;
        final var expectedExceptionErrorMessage0 = "[File] with id [%s] not found".formatted(expectedFileIdValue);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.empty());

        final var input = new DownloadFileChunkInput(
                expectedFileIdValue,
                expectedChunkIndex);

        final var actualException = assertThrows(NotFoundException.class, () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrrosCount, actualException.getErrors().size());
        assertEquals(expectedExceptionErrorMessage0, actualException.getErrors().get(0).message());

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());
        verify(port, times(0)).tryIncrement(any(Identifier.class), any(Integer.class), any(String[].class));
        verify(port, times(0)).getCurrentCount(any());
        verify(port, times(0)).increment(any());
        verify(port, times(0)).decrement(any());
        verify(chunkReader, times(0)).readChunk(
                any(),
                any(),
                any(),
                any());

    }

}
