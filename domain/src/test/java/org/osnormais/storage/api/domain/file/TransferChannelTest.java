package org.osnormais.storage.api.domain.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

public class TransferChannelTest {

    @Test
    void givenThroughputLimitAndChunkSpecificationNull_whenCallsCreate_thenShouldHandlerAppendTwoErrors() {

        final var expectedExcpetionMessage = "'TransferChannel' validation failed";

        final var expectedErrorCount = 2;

        final var expetedErrorMessage1 = "'throughputLimit' should not be null";
        final var expetedErrorMessage2 = "'chunkSpecification' should not be null";

        final ThroughputLimit expectedThroughputLimit = null;
        final ChunkSpecification expectedChunkSpecification = null;

        final var actualException = assertThrows(
                ValidationException.class,
                () -> TransferChannel.create(expectedThroughputLimit, expectedChunkSpecification));

        assertEquals(expectedExcpetionMessage, actualException.getMessage());
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expetedErrorMessage1, actualException.getErrors().get(0).message());
        assertEquals(expetedErrorMessage2, actualException.getErrors().get(1).message());

    }

}
