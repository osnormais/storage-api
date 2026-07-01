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
import org.osnormais.storage.api.application.usecase.file.publish.PublishFileInput;
import org.osnormais.storage.api.application.usecase.file.publish.PublishFileUseCase;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("draft/files")
@RestController
public class DraftFileController {

    private final CreateFileUseCase createFileUseCase;
    private final UploadFileChunkUseCase uploadFileChunkUseCase;
    private final DownloadFileChunkUseCase downloadFileChunkUseCase;
    private final PublishFileUseCase publishFileUseCase;

    public DraftFileController(
            CreateFileUseCase createFileUseCase,
            UploadFileChunkUseCase uploadFileChunkUseCase,
            DownloadFileChunkUseCase downloadFileChunkUseCase,
            PublishFileUseCase publishFileUseCase) {
        this.createFileUseCase = createFileUseCase;
        this.uploadFileChunkUseCase = uploadFileChunkUseCase;
        this.downloadFileChunkUseCase = downloadFileChunkUseCase;
        this.publishFileUseCase = publishFileUseCase;
    }

    @Operation(summary = "Publish file", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("{fileId}/publish")
    public ResponseEntity<Void> publishFile(@RequestHeader UUID fileId) {

        publishFileUseCase.execute(new PublishFileInput(fileId));

        return ResponseEntity.noContent().build();

    }

    @GetMapping("{fileId}/chunks")
    public ResponseEntity<StreamingResponseBody> getChunkInputStream(
            @RequestHeader UUID fileId,
            @RequestHeader Long chunkSize,
            @RequestHeader Long chunkOffset,
            @RequestHeader Integer maxParallelChunks,
            @RequestHeader Long throughputLimit) {

        final var input = new DownloadFileChunkInput(
                fileId,
                chunkSize,
                chunkOffset,
                maxParallelChunks,
                throughputLimit);

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

    @PostMapping("{fileId}/chunks")
    public ResponseEntity<Void> uploadFileChunk(
            @PathVariable("fileId") UUID fileId,
            @RequestHeader("X-Chunk-Index") Long chunkIndex,
            @RequestHeader("X-Chunk-Size") Long chunkSize,
            @RequestHeader("X-Max-Parallel-Chunks") Integer maxParallelChunks,
            @RequestHeader("X-Throughput-Limit") Long throughputLimit,
            @RequestHeader("X-Checksum-Value") String checksumValue,
            @RequestHeader("X-Checksum-Algorithm") Checksum.Algorithm checksumAlgorithm,
            HttpServletRequest request) throws IOException {

        final UploadFileChunkInput input = new UploadFileChunkInput(
                fileId,
                chunkIndex,
                chunkSize,
                maxParallelChunks,
                throughputLimit,
                request.getInputStream(),
                checksumAlgorithm,
                checksumValue);

        uploadFileChunkUseCase.execute(input);

        return ResponseEntity
                .noContent()
                .build();

    }

}
