package org.osnormais.storage.api.infrastructure.configuration.application.usecase;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.DefaultUploadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.UploadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.create.DefaultCreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.complete.CompleteFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.complete.DefaultCompleteFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create.CreateFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create.DefaultCreateFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.domain.event.DomainEventDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUseCaseConfig {

    private final FileCommandGateway fileCommandGateway;
    private final FileQueryGateway fileQueryGateway;
    private final ConcurrencyTracker.Port concurrencyTrackerPort;
    private final ChunkWriter chunkWriter;
    private final DomainEventDispatcher domainEventDispatcher;

    public FileUseCaseConfig(
            final FileCommandGateway fileCommandGateway,
            final FileQueryGateway fileQueryGateway,
            final ConcurrencyTracker.Port concurrencyTrackerPort,
            final ChunkWriter chunkWriter,
            final DomainEventDispatcher domainEventDispatcher) {
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.concurrencyTrackerPort = requireNonNull(concurrencyTrackerPort);
        this.chunkWriter = requireNonNull(chunkWriter);
        this.domainEventDispatcher = requireNonNull(domainEventDispatcher);
    }

    @Bean
    CreateFileUseCase createFileUseCase() {
        return new DefaultCreateFileUseCase(fileCommandGateway);
    }

    @Bean
    CreateFileUploadTransferChannelUseCase createFileUploadTransferChannelUseCase() {
        return new DefaultCreateFileUploadTransferChannelUseCase(fileQueryGateway, fileCommandGateway);
    }

    @Bean
    UploadFileChunkUseCase uploadFileChunkUseCase() {
        return new DefaultUploadFileChunkUseCase(
                fileQueryGateway,
                new ConcurrencyTracker(concurrencyTrackerPort, "chunk-upload"),
                chunkWriter);
    }

    @Bean
    CompleteFileUploadTransferChannelUseCase completeFileUploadTransferChannelUseCase() {
        return new DefaultCompleteFileUploadTransferChannelUseCase(
                fileQueryGateway,
                fileCommandGateway,
                domainEventDispatcher);
    }

}
