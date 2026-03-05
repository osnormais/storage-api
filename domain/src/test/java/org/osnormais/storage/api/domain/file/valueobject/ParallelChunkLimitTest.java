package org.osnormais.storage.api.domain.file.valueobject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class ParallelChunkLimitTest {

    @Test
    void givenAValueLessThanOne_whenCallsValidate_thenHandlerShouldAppendError() {

        final var expectedErrorMessage = "'value' must be greater than or equal to 1";
        final var expectedErrorCount = 1;

        final var expectedValue = 0;

        final var parallelChunkLimit = new ParallelChunkLimit(expectedValue);

        final var handler = Notification.create();
        assertDoesNotThrow(() -> parallelChunkLimit.validate(handler));

        final var actualErrorCount = handler.getErrors().size();
        final var actualErrorMessage = handler.getErrors().get(0).message();

        assertEquals(expectedErrorCount, actualErrorCount);
        assertEquals(expectedErrorMessage, actualErrorMessage);

    }

    @Test
    void givenAValidValue_whenCallsValidate_thenHandlerShouldNotAppendError() {

        final var expectedErrorCount = 0;

        final var expectedValue = 1;

        final var parallelChunkLimit = new ParallelChunkLimit(expectedValue);

        final var handler = Notification.create();
        assertDoesNotThrow(() -> parallelChunkLimit.validate(handler));

        final var actualErrorCount = handler.getErrors().size();

        assertEquals(expectedErrorCount, actualErrorCount);

    }

}
