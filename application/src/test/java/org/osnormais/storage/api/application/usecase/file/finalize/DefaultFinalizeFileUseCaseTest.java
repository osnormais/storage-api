package org.osnormais.storage.api.application.usecase.file.finalize;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.osnormais.storage.api.domain.exception.FileAlreadyPublishedException;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.event.FilePublishedEvent;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.domain.file.valueobject.Publication;
import org.osnormais.storage.api.domain.file.valueobject.Size;

@ExtendWith(MockitoExtension.class)
public class DefaultFinalizeFileUseCaseTest {

    @InjectMocks
    DefaultFinalizeFileUseCase useCase;

    @Mock
    FileQueryGateway fileQueryGateway;

    @Mock
    FileCommandGateway fileCommandGateway;

    @Mock
    FileFinalizer fileFinalizer;

    @Mock
    DomainEventDispatcher eventDispatcher;

    @Test
    void givenAnValidInput_whenCallsExecute_thenShouldFinalizeFileAndNotifyEvent() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileChecksum = Checksum.of(Algorithm.MD5, "checksumMD5");

        final var expectedEventType = FilePublishedEvent.class;
        final var expectedEventKey = FilePublishedEvent.eventKey();

        final TransferChannel uploadTransferChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                Size.of(2048L),
                expectedFileChecksum,
                null,
                uploadTransferChannel,
                null,
                null);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        when(fileCommandGateway.update(expectedFile))
                .thenReturn(expectedFile);

        when(fileFinalizer.finalize(expectedFile))
                .thenReturn(expectedFileChecksum);

        doNothing()
                .when(eventDispatcher)
                .notify(expectedFile);

        final var input = new FinalizeFileInput(expectedFileIdValue);

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
    void givenANonExistentFileId_whenCallsExecuteWithPublishedFile_thenShouldThrowsNotFoundException() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);

        final var expectedExcpetionMessage = "[%s] not found".formatted(File.class.getSimpleName());
        final var expectedErrorsCount = 1;
        final var expectedExceptionErrorMessage0 = "[%s] with id [%s] not found"
                .formatted(
                        File.class.getSimpleName(),
                        expectedFileIdValue.toString());

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.empty());

        final var input = new FinalizeFileInput(expectedFileIdValue);

        final var actualException = assertThrows(NotFoundException.class, () -> useCase.execute(input));

        assertEquals(expectedExcpetionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedExceptionErrorMessage0, actualException.getErrors().get(0).message());

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileCommandGateway, times(0)).update(any());
        verify(fileFinalizer, times(0)).finalize(any());
        verify(eventDispatcher, times(0)).notify(any(File.class));

    }

    @Test
    void givenAnValidInput_whenCallsExecuteWithPublishedFile_thenShouldThrowFileAlreadyPublishedException() {

        final var expectedFileIdValue = UUID.randomUUID();
        final var expectedFileId = FileId.of(expectedFileIdValue);
        final var expectedFileChecksum = Checksum.of(Algorithm.MD5, "checksumMD5");

        final var expectedExcpetionMessage = "File [%s], already published".formatted(expectedFileIdValue);
        final var expectedErrorsCount = 0;

        final TransferChannel uploadTransferChannel = null;

        final var expectedFile = File.with(
                expectedFileId,
                Size.of(2048L),
                expectedFileChecksum,
                Publication.ok(),
                uploadTransferChannel,
                null,
                null);

        when(fileQueryGateway.findById(expectedFileId))
                .thenReturn(Optional.of(expectedFile));

        final var input = new FinalizeFileInput(expectedFileIdValue);

        final var actualException = assertThrows(FileAlreadyPublishedException.class,
                () -> useCase.execute(input));

        assertEquals(expectedExcpetionMessage, actualException.getMessage());
        assertEquals(expectedErrorsCount, actualException.getErrors().size());

        verify(fileQueryGateway, times(1)).findById(expectedFileId);
        verify(fileQueryGateway, times(1)).findById(any());
        verify(fileCommandGateway, times(0)).update(any());
        verify(fileFinalizer, times(0)).finalize(any());
        verify(eventDispatcher, times(0)).notify(any(File.class));

    }

}
