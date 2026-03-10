package org.osnormais.storage.api.domain.file.valueobject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class TransferChannelTest {

    @Test
    void givenThroughputLimitAndChunkSpecificationNull_whenValidate_thenShouldHandlerAppendTwoErrors() {

        final var expectedErrorCount = 2;

        final var expetedErrorMessage1 = "'throughputLimit' should not be null";
        final var expetedErrorMessage2 = "'chunkSpecification' should not be null";

        final ThroughputLimit expectedThroughputLimit = null;
        final ChunkSpecification expectedChunkSpecification = null;

        final var transferChannel = new TransferChannel(expectedThroughputLimit, expectedChunkSpecification);

        final var handler = Notification.create();

        assertDoesNotThrow(() -> transferChannel.validate(handler));

        assertEquals(expectedErrorCount, handler.getErrors().size());
        assertEquals(expetedErrorMessage1, handler.getErrors().get(0).message());
        assertEquals(expetedErrorMessage2, handler.getErrors().get(1).message());

    }

}
