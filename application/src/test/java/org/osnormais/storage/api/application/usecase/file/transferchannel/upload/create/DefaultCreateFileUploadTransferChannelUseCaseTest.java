package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.exception.TransferChannelAlreadyOpennedException;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

@ExtendWith(MockitoExtension.class)
public class DefaultCreateFileUploadTransferChannelUseCaseTest {

    @InjectMocks
    DefaultCreateFileUploadTransferChannelUseCase useCase;

    @Mock
    FileQueryGateway fileQueryGateway;

    @Mock
    FileCommandGateway fileCommandGateway;

    @Test
    void givenAnValidInput_whenCallsExecute_thenShouldCreateUploadTransferChannel() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedThroughputBytesLimitValue = 1024L;
        final var expectedChunkBytesSizeValue = 1024L;
        final var expectedMaxParallelChunksValue = 2;

        final var expectedTotalChunksValue = 3L;
        final var expectedLastChunkBytesSizeValue = 2L;

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSizeValue = 2050L;
        final var expectedFileSize = Size.of(expectedFileSizeValue);
        final var expectedThroughputLimit = ThroughputLimit.create(expectedThroughputBytesLimitValue);
        final var expectedChunkBytesSize = Size.of(expectedChunkBytesSizeValue);
        final var expectedMaxParallelChunks = ParallelChunkLimit.of(expectedMaxParallelChunksValue);
        final var expectedChunkSpecification = ChunkSpecification.create(
                expectedChunkBytesSize,
                expectedMaxParallelChunks);

        final var expectedChecksumValue = "checksumValue";
        final var expectedChecksumAlgorithm = Checksum.Algorithm.MD5;
        final var expectedCheckcum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final var expectedFile = File.with(
                expectedFileId,
                expectedFileSize,
                expectedCheckcum,
                null,
                null,
                null,
                null);

        when(fileQueryGateway.findById(eq(expectedFileId)))
                .thenReturn(Optional.of(expectedFile));

        when(fileCommandGateway.update(
                argThat(file -> {
                    assertEquals(expectedFileId, file.getId());
                    assertEquals(expectedFileSize, file.getSize());
                    assertEquals(expectedCheckcum, file.getChecksum());
                    assertTrue(file.getUploadChannel().isPresent());
                    assertEquals(expectedThroughputLimit,
                            file.getUploadChannel().get().getThroughputLimit());
                    assertEquals(expectedChunkSpecification,
                            file.getUploadChannel().get().getChunkSpecification());
                    return true;
                })))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var input = new CreateFileUploadTransferChannelInput(
                expectedFileIdValue,
                expectedThroughputBytesLimitValue,
                expectedChunkBytesSizeValue,
                expectedMaxParallelChunksValue);

        final var actualOutput = assertDoesNotThrow(() -> useCase.execute(input));

        assertEquals(expectedFileIdValue, actualOutput.fileId());
        assertEquals(expectedTotalChunksValue, actualOutput.totalChunks());
        assertEquals(expectedChunkBytesSizeValue, actualOutput.chunkBytesSize());
        assertEquals(expectedLastChunkBytesSizeValue, actualOutput.lastChunkBytesSize());

    }

    @Test
    void givenANonExistentFileId_whenCallsExecute_thenShouldThrowsNotFoundException() {

        final UUID expectedFileIdValue = UUID.randomUUID();
        final Long expectedThroughputBytesLimit = 1024L;
        final Long expectedChunkBytesSize = 1024L;
        final Integer expectedMaxParallelChunks = 2;

        final var expectedFileId = FileId.of(expectedFileIdValue);

        final var expectedErrorsCount = 1;
        final var expectedExceptionMessage = "[%s] not found".formatted(File.class.getSimpleName());
        final var expectedErrorMessage = "[%s] with id [%s] not found".formatted(
                File.class.getSimpleName(),
                expectedFileId.getStringValue());

        final var input = new CreateFileUploadTransferChannelInput(
                expectedFileIdValue,
                expectedThroughputBytesLimit,
                expectedChunkBytesSize,
                expectedMaxParallelChunks);

        when(fileQueryGateway.findById(eq(expectedFileId)))
                .thenReturn(Optional.empty());

        final var actualException = assertThrows(NotFoundException.class, () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenInvalidInputWithFileIdNull_whenCallsExecute_thenShouldThrowsValidationException() {

        final var expectedExceptionMessage = "Invalid input values";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'id' should not be null";

        final UUID expectedFileIdValue = null;
        final Long expectedThroughputBytesLimit = 1024L;
        final Long expectedChunkBytesSize = 1024L;
        final Integer expectedMaxParallelChunks = 2;

        final var input = new CreateFileUploadTransferChannelInput(
                expectedFileIdValue,
                expectedThroughputBytesLimit,
                expectedChunkBytesSize,
                expectedMaxParallelChunks);

        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenAFileWithUploadTransferChannelAlreadyOpen_whenCallsExecute_thenShouldThrowsTransferChannelAlreadyOpennedException() {

        final var expectedExceptionMessage = "Transfer channel already open";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage0 = "Transfer channel already open, please close the current channel before opening a new one";

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedThroughputBytesLimitValue = 1024L;
        final var expectedChunkBytesSizeValue = 1024L;
        final var expectedMaxParallelChunksValue = 2;

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSizeValue = 2048L;
        final var expectedFileSize = Size.of(expectedFileSizeValue);
        final var expectedThroughputLimit = ThroughputLimit.create(expectedThroughputBytesLimitValue);
        final var expectedChunkBytesSize = Size.of(expectedChunkBytesSizeValue);
        final var expectedMaxParallelChunks = ParallelChunkLimit.of(expectedMaxParallelChunksValue);
        final var expectedChunkSpecification = ChunkSpecification.create(
                expectedChunkBytesSize,
                expectedMaxParallelChunks);

        final var expectedChecksumValue = "checksumValue";
        final var expectedChecksumAlgorithm = Checksum.Algorithm.MD5;
        final var expectedCheckcum = Checksum.of(expectedChecksumAlgorithm, expectedChecksumValue);

        final var transferChannel = TransferChannel.create(expectedThroughputLimit, expectedChunkSpecification);

        final var file = File.with(
                expectedFileId,
                expectedFileSize,
                expectedCheckcum,
                null,
                transferChannel,
                null,
                null);

        when(fileQueryGateway.findById(eq(expectedFileId)))
                .thenReturn(Optional.of(file));

        final var input = new CreateFileUploadTransferChannelInput(
                expectedFileIdValue,
                expectedThroughputBytesLimitValue,
                expectedChunkBytesSizeValue,
                expectedMaxParallelChunksValue);

        final var actualException = assertThrows(TransferChannelAlreadyOpennedException.class,
                () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage0, actualException.getErrors().get(0).message());

    }

}
