package org.osnormais.storage.api.domain.file.valueobject;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.osnormais.storage.api.domain.validation.handler.Notification;

class PublicationTest {

    @Nested
    class Ok {

        @Test
        void givenNothing_whenCallsOk_thenShouldCreateAnOkPublication() {

            final var expectedPublicationStatus = Publication.Status.OK;
            final var expectedPublicationError = Optional.<Publication.Error>empty();

            final var actualPublication = assertDoesNotThrow(() -> Publication.ok());

            assertTrue(nonNull(actualPublication.publishedAt()));
            assertEquals(expectedPublicationStatus, actualPublication.status());
            assertEquals(expectedPublicationError, actualPublication.error());

        }

    }

    @Nested
    class Error {

        @Test
        void givenAValidPublicationError_whenCallsError_thenShouldCreateAnErrorPublication() {

            final var expectedPublicationStatus = Publication.Status.ERROR;
            final var expectedPublicationError = Optional.of(new Publication.Error("An error occurred"));

            final var actualPublication = assertDoesNotThrow(
                    () -> Publication.error(Publication.Error.of("An error occurred")));

            assertTrue(nonNull(actualPublication.publishedAt()));
            assertEquals(expectedPublicationStatus, actualPublication.status());
            assertEquals(expectedPublicationError, actualPublication.error());

        }

    }

    @Nested
    class Validate {

        @Test
        void givenValidPublication_whenValidate_thenShouldNotAppendValidationError() {

            final var expectedValidationErrorsCount = 0;

            final var handler = Notification.create();
            final var actualPublication = Publication.ok();

            assertDoesNotThrow(() -> actualPublication.validate(handler));

            assertEquals(expectedValidationErrorsCount, handler.getErrors().size());

        }

        @Test
        void givenNullPublishedAt_whenValidate_thenShouldAppendValidationError() {

            final var expectedValidationErrorsCount = 1;
            final var expectedValidationErrorMessage0 = "Publication.publishedAt should not be null";

            final Instant expectedPublicationPublishedAt = null;

            final var handler = Notification.create();
            final var actualPublication = new Publication(
                    expectedPublicationPublishedAt,
                    Publication.Status.OK,
                    Optional.empty());

            assertDoesNotThrow(() -> actualPublication.validate(handler));

            assertEquals(expectedPublicationPublishedAt, actualPublication.publishedAt());
            assertEquals(expectedValidationErrorsCount, handler.getErrors().size());
            assertEquals(expectedValidationErrorMessage0, handler.getErrors().get(0).message());

        }

        @Test
        void givenNullStatus_whenValidate_thenShouldAppendValidationError() {

            final var expectedValidationErrorsCount = 1;
            final var expectedValidationErrorMessage0 = "Publication.status should not be null";

            final Publication.Status expectedPublicationStatus = null;

            final var handler = Notification.create();
            final var actualPublication = new Publication(
                    Instant.now(),
                    expectedPublicationStatus,
                    Optional.empty());

            assertDoesNotThrow(() -> actualPublication.validate(handler));

            assertEquals(expectedPublicationStatus, actualPublication.status());
            assertEquals(expectedValidationErrorsCount, handler.getErrors().size());
            assertEquals(expectedValidationErrorMessage0, handler.getErrors().get(0).message());

        }

        @Test
        void givenAnEmptyErrorWithErrorStatus_whenValidate_thenShouldAppendValidationError() {

            final var expectedValidationErrorsCount = 1;
            final var expectedValidationErrorMessage0 = "Publication.error should not be null when status is ERROR";

            final Publication.Status expectedPublicationStatus = Publication.Status.ERROR;

            final var handler = Notification.create();
            final var actualPublication = new Publication(
                    Instant.now(),
                    expectedPublicationStatus,
                    Optional.empty());

            assertDoesNotThrow(() -> actualPublication.validate(handler));

            assertEquals(expectedPublicationStatus, actualPublication.status());
            assertEquals(expectedValidationErrorsCount, handler.getErrors().size());
            assertEquals(expectedValidationErrorMessage0, handler.getErrors().get(0).message());

        }

        @Test
        void givenPopulatedErrorWithOKStatus_whenValidate_thenShouldAppendValidationError() {

            final var expectedValidationErrorsCount = 1;
            final var expectedValidationErrorMessage0 = "Publication.error should be empty when status is OK";

            final Publication.Status expectedPublicationStatus = Publication.Status.OK;

            final var handler = Notification.create();
            final var actualPublication = new Publication(
                    Instant.now(),
                    expectedPublicationStatus,
                    Optional.of(Publication.Error.of("error")));

            assertDoesNotThrow(() -> actualPublication.validate(handler));

            assertEquals(expectedPublicationStatus, actualPublication.status());
            assertEquals(expectedValidationErrorsCount, handler.getErrors().size());
            assertEquals(expectedValidationErrorMessage0, handler.getErrors().get(0).message());

        }

    }

    @Nested
    class PublicationError {

        @Nested
        class Of {

            @Test
            void givenAValidMessage_whenCallsOf_thenShouldCreateAPublicationError() {

                final var expectedPublicationStatus = Publication.Status.ERROR;
                final var expectedPublicationError = Optional.of(new Publication.Error("An error occurred"));

                final var actualPublication = assertDoesNotThrow(
                        () -> Publication.error(Publication.Error.of("An error occurred")));

                assertTrue(nonNull(actualPublication.publishedAt()));
                assertEquals(expectedPublicationStatus, actualPublication.status());
                assertEquals(expectedPublicationError, actualPublication.error());

            }

        }

        @Nested
        class Validate {

            @Test
            void givenNullMessage_whenValidate_thenShouldAppendValidationError() {

                final var expectedValidationErrorsCount = 1;
                final var expectedValidationErrorMessage0 = "Publication.Error.message should not be null";

                final String expectedPublicationErrorMessage = null;

                final var handler = Notification.create();
                final var actualPublicationError = new Publication.Error(expectedPublicationErrorMessage);

                assertDoesNotThrow(() -> actualPublicationError.validate(handler));

                assertEquals(expectedPublicationErrorMessage, actualPublicationError.message());
                assertEquals(expectedValidationErrorsCount, handler.getErrors().size());
                assertEquals(expectedValidationErrorMessage0, handler.getErrors().get(0).message());

            }

            @Test
            void givenBlankMessage_whenValidate_thenShouldAppendValidationError() {

                final var expectedValidationErrorsCount = 1;
                final var expectedValidationErrorMessage0 = "Publication.Error.message should not be blank";

                final String expectedPublicationErrorMessage = " ";

                final var handler = Notification.create();
                final var actualPublicationError = new Publication.Error(expectedPublicationErrorMessage);

                assertDoesNotThrow(() -> actualPublicationError.validate(handler));

                assertEquals(expectedPublicationErrorMessage, actualPublicationError.message());
                assertEquals(expectedValidationErrorsCount, handler.getErrors().size());
                assertEquals(expectedValidationErrorMessage0, handler.getErrors().get(0).message());

            }

        }

    }

}
