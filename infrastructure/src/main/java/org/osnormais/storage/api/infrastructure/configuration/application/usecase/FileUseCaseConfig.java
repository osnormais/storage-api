package org.osnormais.storage.api.infrastructure.configuration.application.usecase;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.application.port.ConcurrencyTracker;
import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.application.usecase.file.chunk.download.DefaultDownloadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.chunk.download.DownloadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.DefaultUploadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.UploadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.create.DefaultCreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.finalize.DefaultFinalizeFileUseCase;
import org.osnormais.storage.api.application.usecase.file.finalize.FinalizeFileUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.download.create.CreateFileDownloadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.download.create.DefaultCreateFileDownloadTransferChannelUseCase;
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
    private final ChunkReader chunkReader;
    private final FileFinalizer fileFinalizer;
    private final DomainEventDispatcher domainEventDispatcher;

    public FileUseCaseConfig(
            final FileCommandGateway fileCommandGateway,
            final FileQueryGateway fileQueryGateway,
            final ConcurrencyTracker.Port concurrencyTrackerPort,
            final ChunkWriter chunkWriter,
            final ChunkReader chunkReader,
            final FileFinalizer fileFinalizer,
            final DomainEventDispatcher domainEventDispatcher) {
        this.fileCommandGateway = requireNonNull(fileCommandGateway);
        this.fileQueryGateway = requireNonNull(fileQueryGateway);
        this.concurrencyTrackerPort = requireNonNull(concurrencyTrackerPort);
        this.chunkWriter = requireNonNull(chunkWriter);
        this.chunkReader = requireNonNull(chunkReader);
        this.fileFinalizer = requireNonNull(fileFinalizer);
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

    @Bean
    FinalizeFileUseCase finalizeFileUseCase() {
        return new DefaultFinalizeFileUseCase(
                fileQueryGateway,
                fileCommandGateway,
                fileFinalizer,
                domainEventDispatcher);
    }

    @Bean
    CreateFileDownloadTransferChannelUseCase createFileDownloadTransferChannelUseCase() {
        return new DefaultCreateFileDownloadTransferChannelUseCase(
                fileQueryGateway,
                fileCommandGateway);
    }

    @Bean
    DownloadFileChunkUseCase downloadFileChunkUseCase() {
        return new DefaultDownloadFileChunkUseCase(
                fileQueryGateway,
                new ConcurrencyTracker(concurrencyTrackerPort, "chunk-download"),
                chunkReader);
    }

}
