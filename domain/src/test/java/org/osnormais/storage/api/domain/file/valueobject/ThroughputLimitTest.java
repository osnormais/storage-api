package org.osnormais.storage.api.domain.file.valueobject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class ThroughputLimitTest {

    @Test
    void givenAValueLessThanOne_whenCallsValidate_thenHandlerShouldAppendError() {

        final var expectedErrorMessage = "bytesPerSecond must be greater than or equal to 0";
        final var expectedErrorCount = 1;

        final var expectedBytesPerSecond = 0L;

        final var throughputLimit = new ThroughputLimit(expectedBytesPerSecond);

        final var handler = Notification.create();
        assertDoesNotThrow(() -> throughputLimit.validate(handler));

        final var actualErrorCount = handler.getErrors().size();
        final var actualErrorMessage = handler.getErrors().get(0).message();

        assertEquals(expectedErrorCount, actualErrorCount);
        assertEquals(expectedErrorMessage, actualErrorMessage);

    }

    @Test
    void givenAValidValue_whenCallsValidate_thenHandlerShouldNotAppendError() {

        final var expectedErrorCount = 0;

        final var expectedBytesPerSecond = 1L;

        final var throughputLimit = new ThroughputLimit(expectedBytesPerSecond);

        final var handler = Notification.create();
        assertDoesNotThrow(() -> throughputLimit.validate(handler));

        final var actualErrorCount = handler.getErrors().size();

        assertEquals(expectedErrorCount, actualErrorCount);

    }

}
