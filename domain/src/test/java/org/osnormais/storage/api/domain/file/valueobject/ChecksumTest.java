package org.osnormais.storage.api.domain.file.valueobject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.domain.validation.handler.Notification;

public class ChecksumTest {

    @Test
    void givenAnNullAlgorithmAndNullValue_whenValidate_thenHandlerShouldAppendTwoErrors() {

        final var expectedErrorMessage1 = "'algorithm' should not be null";
        final var expectedErrorMessage2 = "'value' should not be null";

        final var expectedErrorsCount = 2;

        final Algorithm expectedAlgorithm = null;
        final String expectedValue = null;

        final var actualChecksum = assertDoesNotThrow(() -> new Checksum(expectedAlgorithm, expectedValue));

        final var actualValidationHandler = Notification.create();
        actualChecksum.validate(actualValidationHandler);

        final var actualErrors = actualValidationHandler.getErrors();
        final var actualErrorsCount = actualErrors.size();

        assertEquals(expectedErrorsCount, actualErrorsCount);
        assertEquals(expectedErrorMessage1, actualErrors.get(0).message());
        assertEquals(expectedErrorMessage2, actualErrors.get(1).message());

    }

    @Test
    void givenAValidParams_whenValidate_thenHandlerShouldNotAppendError() {

        final var expectedErrorsCount = 0;

        final Algorithm expectedAlgorithm = Algorithm.MD5;
        final String expectedValue = "some-value";

        final var actualChecksum = assertDoesNotThrow(() -> new Checksum(expectedAlgorithm, expectedValue));

        final var actualValidationHandler = Notification.create();
        actualChecksum.validate(actualValidationHandler);

        final var actualErrors = actualValidationHandler.getErrors();
        final var actualErrorsCount = actualErrors.size();

        assertEquals(expectedErrorsCount, actualErrorsCount);

    }

}
