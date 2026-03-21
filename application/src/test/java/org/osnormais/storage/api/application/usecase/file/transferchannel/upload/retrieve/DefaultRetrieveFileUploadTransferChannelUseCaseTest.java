package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.osnormais.storage.api.application.exception.TransferChannelNotAvailableException;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;

@ExtendWith(MockitoExtension.class)
public class DefaultRetrieveFileUploadTransferChannelUseCaseTest {

    @InjectMocks
    DefaultRetrieveFileUploadTransferChannelUseCase useCase;

    @Mock
    FileQueryGateway fileQueryGateway;

    @Test
    void givenAnExistingFileWithUploadChannel_whenCallsExecute_thenShouldReturnUploadTransferChannel() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedThroughputBytesLimit = 1024L;
        final var expectedChunkBytesSize = 100L;
        final var expectedMaxParallelChunks = 2;

        final var expectedFileSizeValue = 2050L;
        final var expectedTotalChunks = 21L;
        final var expectedLastChunkBytesSize = 50L;

        final var expectedTransferChannel = TransferChannel.create(
                ThroughputLimit.create(expectedThroughputBytesLimit),
                ChunkSpecification.create(
                        Size.of(expectedChunkBytesSize),
                        ParallelChunkLimit.of(expectedMaxParallelChunks)));

        final var expectedFile = File.with(
                expectedFileId,
                Size.of(expectedFileSizeValue),
                Checksum.of(Checksum.Algorithm.CRC_32, "checksum"),
                expectedTransferChannel,
                null,
                null);

        when(fileQueryGateway.findById(eq(expectedFileId)))
                .thenReturn(Optional.of(expectedFile));

        final var input = new RetrieveFileUploadTransferChannelInput(expectedFileIdValue);

        final var actualOutput = assertDoesNotThrow(() -> useCase.execute(input));

        assertEquals(expectedFileIdValue, actualOutput.fileId());
        assertEquals(expectedThroughputBytesLimit, actualOutput.throughputBytesLimit());
        assertEquals(expectedTotalChunks, actualOutput.totalChunks());
        assertEquals(expectedChunkBytesSize, actualOutput.chunkBytesSize());
        assertEquals(expectedLastChunkBytesSize, actualOutput.lastChunkBytesSize());
        assertEquals(expectedMaxParallelChunks, actualOutput.maxParallelChunks());

    }

    @Test
    void givenANonExistentFileId_whenCallsExecute_thenShouldThrowsNotFoundException() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);

        final var expectedErrorsCount = 1;
        final var expectedExceptionMessage = "[%s] not found".formatted(File.class.getSimpleName());
        final var expectedErrorMessage = "[%s] with id [%s] not found".formatted(
                File.class.getSimpleName(),
                expectedFileId.getStringValue());

        when(fileQueryGateway.findById(eq(expectedFileId)))
                .thenReturn(Optional.empty());

        final var input = new RetrieveFileUploadTransferChannelInput(expectedFileIdValue);

        final var actualException = assertThrows(NotFoundException.class, () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenAFileWithoutUploadChannel_whenCallsExecute_thenShouldThrowsTransferChannelNotAvailableException() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedErrorMessage = "[upload]Transfer channel of File=[%s] is not available"
                .formatted(expectedFileId.getStringValue());

        final var expectedFile = File.with(
                expectedFileId,
                Size.of(2048L),
                Checksum.of(Checksum.Algorithm.CRC_32, "checksum"),
                null,
                null,
                null);

        when(fileQueryGateway.findById(eq(expectedFileId)))
                .thenReturn(Optional.of(expectedFile));

        final var input = new RetrieveFileUploadTransferChannelInput(expectedFileIdValue);

        final var actualException = assertThrows(TransferChannelNotAvailableException.class, () -> useCase.execute(input));

        assertEquals("Transfer channel is not available.", actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().getFirst().message());

    }

}

