package org.osnormais.storage.api.application.port;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osnormais.storage.api.domain.Identifier;

@ExtendWith(MockitoExtension.class)
class ConcurrencyTrackerTest {

    ConcurrencyTracker concurrencyTracker;

    String[] tags;
    ConcurrencyTracker.Port port;

    @BeforeEach
    void setup() {
        tags = new String[] { "upload", "chunk" };
        port = Mockito.mock(ConcurrencyTracker.Port.class);
        concurrencyTracker = new ConcurrencyTracker(port, tags);
    }

    @Test
    void givenAValidKey_whenCallsIncrement_shouldIncremetKeyWithTags() {

        final var expectedIdentifier = new ConcurrencyTrackerTestIdentifier("123");

        doNothing()
                .when(port)
                .increment(expectedIdentifier, tags);

        assertDoesNotThrow(() -> concurrencyTracker.increment(expectedIdentifier));

        verify(port, times(1)).increment(expectedIdentifier, tags);
        verify(port, times(0)).decrement(any(), any());
        verify(port, times(0)).getCurrentCount(any(), any());

    }

    @Test
    void givenAValidKey_whenCallsDecrement_shouldDecrementKeyWithTags() {

        final var expectedIdentifier = new ConcurrencyTrackerTestIdentifier("123");

        doNothing()
                .when(port)
                .decrement(expectedIdentifier, tags);

        assertDoesNotThrow(() -> concurrencyTracker.decrement(expectedIdentifier));

        verify(port, times(1)).decrement(expectedIdentifier, tags);
        verify(port, times(0)).increment(any(), any());
        verify(port, times(0)).getCurrentCount(any(), any());

    }

    @Test
    void givenAValidKey_whenCallsGetCurrentCount_shouldGetCurrentCountTags() {

        final var expectedIdentifier = new ConcurrencyTrackerTestIdentifier("123");
        final var expectedCurrentCount = 1;

        when(port.getCurrentCount(expectedIdentifier, tags))
                .thenReturn(expectedCurrentCount);

        final var actualCurrentCount = assertDoesNotThrow(() -> concurrencyTracker.getCurrentCount(expectedIdentifier));

        assertEquals(expectedCurrentCount, actualCurrentCount);

        verify(port, times(1)).getCurrentCount(expectedIdentifier, tags);
        verify(port, times(0)).decrement(any(), any());
        verify(port, times(0)).increment(any(), any());

    }

    static class ConcurrencyTrackerTestIdentifier extends Identifier<String> {

        ConcurrencyTrackerTestIdentifier(String id) {
            super(id);
        }

        @Override
        public String getStringValue() {
            return super.getValue();
        }
    }
}