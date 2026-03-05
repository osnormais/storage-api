package org.osnormais.storage.api.domain.file.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ChunkSpecificationTest {

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

}
