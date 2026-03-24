package org.osnormais.storage.api.application.usecase.file.create;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;

@ExtendWith(MockitoExtension.class)
public class DefaultCreateFileUseCaseTest {

    @InjectMocks
    DefaultCreateFileUseCase useCase;

    @Mock
    FileCommandGateway fileCommandGateway;

    @Test
    void givenAnValidInput_whenCallsExecute_thenShouldCreateFile() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileSizeInBytesValue = 1024L;
        final var expectedFileChecksumValue = "checksumValue";
        final var expectedFileChecksumAlgorithm = Checksum.Algorithm.CRC_32;

        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileSize = Size.of(expectedFileSizeInBytesValue);
        final var expectedFileChecksum = Checksum.of(expectedFileChecksumAlgorithm, expectedFileChecksumValue);

        when(fileCommandGateway
                .create(argThat(file -> {

                    assertEquals(expectedFileId, file.getId());
                    assertEquals(expectedFileSize, file.getSize());
                    assertEquals(expectedFileChecksum, file.getChecksum());
                    assertTrue(file.getUploadChannel().isEmpty());
                    assertTrue(file.getDownloadChannel().isEmpty());

                    return true;

                }))).thenAnswer(invocation -> invocation.getArgument(0));

        final var input = new CreateFileInput(
                expectedFileIdValue,
                expectedFileSizeInBytesValue,
                expectedFileChecksumValue,
                expectedFileChecksumAlgorithm);

        final var actualOutput = assertDoesNotThrow(() -> useCase.execute(input));

        assertEquals(expectedFileIdValue, actualOutput.id());

        verify(fileCommandGateway, times(1)).create(any());

    }

    @Test
    void givenAnInvalidSizeInBytesInput_whenCallsExecute_thenShouldThrowsValidationException() {

        final var expectedExceptionMessage = "Failed to create File";
        final var expectedErrrosCount = 1;
        final var expectedErrorMessage0 = "bytes must be greater than 0";

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileSizeInBytesValue = -1024L;
        final var expectedFileChecksumValue = "checksumValue";
        final var expectedFileChecksumAlgorithm = Checksum.Algorithm.CRC_32;

        final var input = new CreateFileInput(
                expectedFileIdValue,
                expectedFileSizeInBytesValue,
                expectedFileChecksumValue,
                expectedFileChecksumAlgorithm);

        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(input));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrrosCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage0, actualException.getErrors().get(0).message());

        verify(fileCommandGateway, times(0)).create(any());

    }

}
