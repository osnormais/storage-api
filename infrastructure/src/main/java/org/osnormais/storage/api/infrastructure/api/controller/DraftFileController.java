package org.osnormais.storage.api.infrastructure.api.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import org.osnormais.storage.api.application.usecase.file.chunk.download.DownloadFileChunkInput;
import org.osnormais.storage.api.application.usecase.file.chunk.download.DownloadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.UploadFileChunkInput;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.UploadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileInput;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileOutput;
import org.osnormais.storage.api.application.usecase.file.create.CreateFileUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.download.create.CreateFileDownloadTransferChannelInput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.download.create.CreateFileDownloadTransferChannelOutput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.download.create.CreateFileDownloadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.complete.CompleteFileUploadTransferChannelInput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.complete.CompleteFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create.CreateFileUploadTransferChannelInput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create.CreateFileUploadTransferChannelOutput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create.CreateFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve.RetrieveFileUploadTransferChannelInput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve.RetrieveFileUploadTransferChannelOutput;
import org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve.RetrieveFileUploadTransferChannelUseCase;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("draft/files")
@RestController
public class DraftFileController {

    private final CreateFileUseCase createFileUseCase;
    private final CreateFileUploadTransferChannelUseCase createFileUploadTransferChannelUseCase;
    private final RetrieveFileUploadTransferChannelUseCase retrieveFileUploadTransferChannelUseCase;
    private final CompleteFileUploadTransferChannelUseCase completeFileUploadTransferChannelUseCase;
    private final UploadFileChunkUseCase uploadFileChunkUseCase;
    private final DownloadFileChunkUseCase downloadFileChunkUseCase;
    private final CreateFileDownloadTransferChannelUseCase createFileDownloadTransferChannelUseCase;

    public DraftFileController(
            CreateFileUseCase createFileUseCase,
            CreateFileUploadTransferChannelUseCase createFileUploadTransferChannelUseCase,
            RetrieveFileUploadTransferChannelUseCase retrieveFileUploadTransferChannelUseCase,
            CompleteFileUploadTransferChannelUseCase completeFileUploadTransferChannelUseCase,
            UploadFileChunkUseCase uploadFileChunkUseCase,
            DownloadFileChunkUseCase downloadFileChunkUseCase,
            CreateFileDownloadTransferChannelUseCase createFileDownloadTransferChannelUseCase) {
        this.createFileUseCase = createFileUseCase;
        this.createFileUploadTransferChannelUseCase = createFileUploadTransferChannelUseCase;
        this.retrieveFileUploadTransferChannelUseCase = retrieveFileUploadTransferChannelUseCase;
        this.completeFileUploadTransferChannelUseCase = completeFileUploadTransferChannelUseCase;
        this.uploadFileChunkUseCase = uploadFileChunkUseCase;
        this.downloadFileChunkUseCase = downloadFileChunkUseCase;
        this.createFileDownloadTransferChannelUseCase = createFileDownloadTransferChannelUseCase;
    }

    @GetMapping
    public ResponseEntity<StreamingResponseBody> getChunkInputStream(
            @RequestHeader UUID fileId,
            @RequestHeader Long chunkIndex) {

        final var input = new DownloadFileChunkInput(fileId, chunkIndex);

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = downloadFileChunkUseCase.execute(input).data()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            }
        };

        return ResponseEntity.ok().body(responseBody);

    }

    @PostMapping
    public ResponseEntity<Void> createFile(@RequestBody CreateFileInput input) {

        CreateFileOutput output = createFileUseCase.execute(input);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(output.id().toString())
                .toUri();

        return ResponseEntity.created(location).build();

    }

    @PostMapping("upload-transfer-channel")
    public ResponseEntity<CreateFileUploadTransferChannelOutput> createFileUploadTransferChannel(
            @RequestBody CreateFileUploadTransferChannelInput input) {
        return ResponseEntity
                .ok()
                .body(createFileUploadTransferChannelUseCase.execute(input));
    }

    @GetMapping("{fileId}/upload-transfer-channel")
    public ResponseEntity<RetrieveFileUploadTransferChannelOutput> retrieveFileUploadTransferChannel(
            @PathVariable UUID fileId) {

        final var input = new RetrieveFileUploadTransferChannelInput(fileId);

        return ResponseEntity
                .ok()
                .body(retrieveFileUploadTransferChannelUseCase.execute(input));
    }

    @PostMapping("upload-transfer-channel/complete")
    public ResponseEntity<Void> completeFileUploadTransferChannel(
            @RequestBody CompleteFileUploadTransferChannelInput input) {

        completeFileUploadTransferChannelUseCase.execute(input);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("{fileId}/chunks/{chunkIndex}")
    public ResponseEntity<Void> uploadFileChunk(
            @PathVariable("fileId") UUID fileId,
            @PathVariable("chunkIndex") Long chunkIndex,
            @RequestHeader("X-Checksum-Value") String checksumValue,
            @RequestHeader("X-Checksum-Algorithm") Checksum.Algorithm checksumAlgorithm,
            HttpServletRequest request) throws IOException {

        UploadFileChunkInput input = new UploadFileChunkInput(
                fileId,
                chunkIndex,
                request.getInputStream(),
                checksumAlgorithm,
                checksumValue);

        uploadFileChunkUseCase.execute(input);

        return ResponseEntity
                .noContent()
                .build();

    }

    @PostMapping("download-transfer-channel")
    public ResponseEntity<CreateFileDownloadTransferChannelOutput> createFileDownloadTransferChannel(
            @RequestBody CreateFileDownloadTransferChannelInput input) {
        return ResponseEntity
                .ok()
                .body(createFileDownloadTransferChannelUseCase.execute(input));
    }

}
