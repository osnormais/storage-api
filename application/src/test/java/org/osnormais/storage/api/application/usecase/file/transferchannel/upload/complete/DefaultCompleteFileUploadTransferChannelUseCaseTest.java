package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.complete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osnormais.storage.api.application.exception.NotFoundException;
import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.event.DomainEventSource;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.event.FileUploadTransferChannelCompletedEvent;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;

@ExtendWith(MockitoExtension.class)
public class DefaultCompleteFileUploadTransferChannelUseCaseTest {

    @InjectMocks
    DefaultCompleteFileUploadTransferChannelUseCase useCase;

    @Mock
    FileQueryGateway fileQueryGateway;

    @Mock
    FileCommandGateway fileCommandGateway;

    @Mock
    DomainEventDispatcher eventDispatcher;

    @Test
    void givenAnValidInput_whenFileHasUploadTransferChannel_thenShouldCompleteUploadTransferChannelAndNotifyEvent() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);

        final var expectedEventType = FileUploadTransferChannelCompletedEvent.class;
        final var expectedEventKey = FileUploadTransferChannelCompletedEvent.eventKey();

        final var expectedThroughputLimitBytesPerSecondValue = 1024L;

        final var expectedChunkSpecificationSizeValue = 100L;
        final var expectedParallelChunkLimitValue = 2;

        final var expectedChunkSpecification = ChunkSpecification.create(
                Size.of(expectedChunkSpecificationSizeValue),
                ParallelChunkLimit.of(expectedParallelChunkLimitValue));

        final var uploadTransferChannel = TransferChannel.create(
                ThroughputLimit.create(expectedThroughputLimitBytesPerSecondValue),
                expectedChunkSpecification);

        final var expectedFile = File.with(
                expectedFileId,
                Size.of(2048L),
                Checksum.of(Algorithm.MD5, "checksumMD5"),
                false,
                uploadTransferChannel,
                null,
                null);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(fileCommandGateway.update(expectedFile))
                .thenAnswer(returnsFirstArg());

        doNothing()
                .when(eventDispatcher)
                .notify(expectedFile);

        final var input = new CompleteFileUploadTransferChannelInput(expectedFileIdValue);

        assertDoesNotThrow(() -> useCase.execute(input));

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileCommandGateway, times(1)).update(expectedFile);
        verify(fileCommandGateway, times(1)).update(any());

        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        verify(eventDispatcher, times(1)).notify(captor.capture());
        verify(eventDispatcher, times(1)).notify(any(File.class));

        final var eventDispatcherArg = captor.getValue();
        final var firstEvent = eventDispatcherArg.nextEvent();

        assertEquals(expectedFileId, eventDispatcherArg.getId());
        assertTrue(firstEvent.isPresent());
        assertEquals(expectedEventType, firstEvent.get().getClass());
        assertEquals(expectedEventKey, firstEvent.get().key());
        assertEquals(expectedFileId, firstEvent.get().getIdentifier());
        assertTrue(eventDispatcherArg.nextEvent().isEmpty());

    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAInexistentFileId_whenCallsExecute_thenShouldThrowsNotFoundException() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.empty());

        final var input = new CompleteFileUploadTransferChannelInput(expectedFileIdValue);

        assertThrows(NotFoundException.class, () -> useCase.execute(input));

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileCommandGateway, times(0)).update(any());
        verify(eventDispatcher, times(0)).notify(any(DomainEventSource.class));
        verify(eventDispatcher, times(0)).notify(any(DomainEvent.class));

    }

}
