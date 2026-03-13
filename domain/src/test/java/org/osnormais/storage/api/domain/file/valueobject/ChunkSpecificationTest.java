package org.osnormais.storage.api.domain.file.valueobject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class ChunkSpecificationTest {

    @Test
    void givenSizeAndMaxParallelNull_whenValidate_thenShouldHandlerAppendTwoErrors() {

        final var expectedErrorCount = 2;

        final var expetedErrorMessage1 = "Chunk 'size' should not be null";
        final var expetedErrorMessage2 = "Chunk 'maxParallel' should not be null";

        final Size expectedChunkSize = null;
        final ParallelChunkLimit expectedParallelChunkLimit = null;

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var handler = Notification.create();

        assertDoesNotThrow(() -> chunkSpecification.validate(handler));

        assertEquals(expectedErrorCount, handler.getErrors().size());
        assertEquals(expetedErrorMessage1, handler.getErrors().get(0).message());
        assertEquals(expetedErrorMessage2, handler.getErrors().get(1).message());

    }

    @Test
    void givenFileSizeWithPartialChunk_whenCalculatingLastChunk_thenReturnsReminder() {

        final var expectedLastChunkSize = new Size(5L);

        final var actualChunkSize = new Size(25L);
        final var actualParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(actualChunkSize, actualParallelChunkLimit);

        final var fileSize = new Size(105L);

        final var actualLastChunkSize = chunkSpecification.lastChunkSize(fileSize);

        assertEquals(expectedLastChunkSize, actualLastChunkSize);

    }

    @Test
    void givenFileSizeWithEvenDivision_whenCalculatingLastChunk_thenReturnsFullChunkSize() {

        final var expectedLastChunkSize = new Size(25L);

        final var actualChunkSize = new Size(25L);
        final var actualParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(actualChunkSize, actualParallelChunkLimit);

        final var fileSize = new Size(100L);

        final var actualLastChunkSize = chunkSpecification.lastChunkSize(fileSize);

        assertEquals(expectedLastChunkSize, actualLastChunkSize);

    }

    @Test
    void givenFileSizeWithPartialChunk_whenCalculatingTotalChunks_thenReturnsReminder() {

        final var expectedTotalChunks = 5L;

        final var actualChunkSize = new Size(25L);
        final var actualParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(actualChunkSize, actualParallelChunkLimit);

        final var fileSize = new Size(105L);

        final var actualTotalChunks = chunkSpecification.totalChunks(fileSize);

        assertEquals(expectedTotalChunks, actualTotalChunks);

    }

    @Test
    void givenFileSizeWithEvenDivision_whenCalculatingTotalChunks_thenReturnsFullChunkSize() {

        final var expectedTotalChunks = 4L;

        final var actualChunkSize = new Size(25L);
        final var actualParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(actualChunkSize, actualParallelChunkLimit);

        final var fileSize = new Size(100L);

        final var actualTotalChunks = chunkSpecification.totalChunks(fileSize);

        assertEquals(expectedTotalChunks, actualTotalChunks);

    }

    @Test
    void givenFileSizeBiggerThanChunkSize_whenCalculatingChunkBytesSize_thenReturnsEffectiveChunkSize() {

        final var expectedChunkBytesSize = Size.of(25L);

        final var actualChunkSize = new Size(25L);
        final var actualParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(actualChunkSize, actualParallelChunkLimit);

        final var fileSize = new Size(100L);

        final var actualChunkBytesSize = chunkSpecification.effectiveChunkSize(fileSize);

        assertEquals(expectedChunkBytesSize, actualChunkBytesSize);

    }

    @Test
    void givenFileSizeSmallerThanChunkSize_whenCalculatingEffectiveChunkSize_thenReturnsFileSize() {

        final var expectedChunkBytesSize = Size.of(100L);

        final var actualChunkSize = new Size(105L);
        final var actualParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(actualChunkSize, actualParallelChunkLimit);

        final var fileSize = new Size(100L);

        final var actualChunkBytesSize = chunkSpecification.effectiveChunkSize(fileSize);

        assertEquals(expectedChunkBytesSize, actualChunkBytesSize);

    }

    @Test
    void givenANullChunkIndex_whenCalculatingEffectiveChunkSize_thenShouldThrowsInvalidArgumentException() {

        final var expectedExceptionMessage = "Invalid argument provided.";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'chunkIndex' should not be null";

        final Long expectedChunkIndex = null;
        final var expectedFileSize = new Size(100L);

        final var expectedChunkSize = new Size(25L);
        final var expectedParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var actualException = assertThrows(
                InvalidArgumentException.class,
                () -> chunkSpecification
                        .effectiveChunkSize(
                                expectedFileSize,
                                expectedChunkIndex));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenANullFileSize_whenCalculatingEffectiveChunkSize_thenShouldThrowsInvalidArgumentException() {

        final var expectedExceptionMessage = "Invalid argument provided.";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'fileSize' should not be null";

        final var expectedChunkIndex = 1L;
        final Size expectedFileSize = null;

        final var expectedChunkSize = new Size(25L);
        final var expectedParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var actualException = assertThrows(
                InvalidArgumentException.class,
                () -> chunkSpecification
                        .effectiveChunkSize(
                                expectedFileSize,
                                expectedChunkIndex));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenAnNegativeChunkIndex_whenCalculatingEffectiveChunkSize_thenShouldThrowsInvalidArgumentException() {

        final var expectedExceptionMessage = "Invalid argument provided.";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'chunkIndex' out of bounds";
        final var expectedChunkIndex = -1L;
        final var expectedFileSize = Size.of(1024L);

        final var expectedChunkSize = new Size(25L);
        final var expectedParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var actualException = assertThrows(
                InvalidArgumentException.class,
                () -> chunkSpecification
                        .effectiveChunkSize(
                                expectedFileSize,
                                expectedChunkIndex));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenAnOutOfBoundChunkIndex_whenCalculatingEffectiveChunkSize_thenShouldThrowsInvalidArgumentException() {

        final var expectedExceptionMessage = "Invalid argument provided.";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'chunkIndex' out of bounds";
        final var expectedChunkIndex = 9999L;
        final var expectedFileSize = Size.of(1024L);

        final var expectedChunkSize = new Size(25L);
        final var expectedParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var actualException = assertThrows(
                InvalidArgumentException.class,
                () -> chunkSpecification
                        .effectiveChunkSize(
                                expectedFileSize,
                                expectedChunkIndex));

        assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    void givenAValidArguments_whenCalculatingEffectiveChunkSize_thenShouldReturnEffectiveLastChunkSize() {

        final var expectedChunkIndex = 4L;
        final var expectedFileSize = Size.of(105);
        final var expectedEffectiveChunkSize = Size.of(5);

        final var expectedChunkSize = new Size(25L);
        final var expectedParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var actualChunkSize = assertDoesNotThrow(
                () -> chunkSpecification
                        .effectiveChunkSize(
                                expectedFileSize,
                                expectedChunkIndex));

        assertEquals(expectedEffectiveChunkSize, actualChunkSize);

    }

    @Test
    void givenAValidArguments_whenCalculatingEffectiveChunkSize_thenShouldReturnEffectiveChunkSize() {

        final var expectedChunkIndex = 0L;
        final var expectedFileSize = Size.of(105);
        final var expectedEffectiveChunkSize = Size.of(25);

        final var expectedChunkSize = new Size(25L);
        final var expectedParallelChunkLimit = new ParallelChunkLimit(1);

        final var chunkSpecification = new ChunkSpecification(expectedChunkSize, expectedParallelChunkLimit);

        final var actualChunkSize = assertDoesNotThrow(
                () -> chunkSpecification
                        .effectiveChunkSize(
                                expectedFileSize,
                                expectedChunkIndex));

        assertEquals(expectedEffectiveChunkSize, actualChunkSize);

    }

}
