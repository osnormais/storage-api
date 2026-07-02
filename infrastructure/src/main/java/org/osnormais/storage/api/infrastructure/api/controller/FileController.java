package org.osnormais.storage.api.infrastructure.api.controller;

import java.io.IOException;
import java.io.InputStream;

import org.osnormais.storage.api.application.usecase.file.chunk.download.DownloadFileChunkInput;
import org.osnormais.storage.api.application.usecase.file.chunk.download.DownloadFileChunkUseCase;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.UploadFileChunkInput;
import org.osnormais.storage.api.application.usecase.file.chunk.upload.UploadFileChunkUseCase;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.infrastructure.api.FileAPI;
import org.osnormais.storage.api.infrastructure.commons.SecurityContext;
import org.osnormais.storage.api.infrastructure.file.data.rest.TransferChannelTokenData;
import org.osnormais.storage.api.infrastructure.token.TokenDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class FileController implements FileAPI {

    private final TokenDecoder<TransferChannelTokenData> tokenDecoder;

    private final UploadFileChunkUseCase uploadFileChunkUseCase;
    private final DownloadFileChunkUseCase downloadFileChunkUseCase;

    public FileController(
            TokenDecoder<TransferChannelTokenData> tokenDecoder,
            UploadFileChunkUseCase uploadFileChunkUseCase,
            DownloadFileChunkUseCase downloadFileChunkUseCase) {
        this.tokenDecoder = tokenDecoder;
        this.uploadFileChunkUseCase = uploadFileChunkUseCase;
        this.downloadFileChunkUseCase = downloadFileChunkUseCase;
    }

    public ResponseEntity<Void> uploadChunk(
            String chunkToken,
            String checksumValue,
            Algorithm checksumAlgorithm,
            HttpServletRequest request) throws IOException {

        final var tokenData = tokenDecoder.decode(chunkToken);

        if (!tokenData.actor().equals(SecurityContext.getAuthenticatedUserId())
                || !"UPLOAD".equalsIgnoreCase(tokenData.type()))
            return ResponseEntity.status(403).build();

        final UploadFileChunkInput input = new UploadFileChunkInput(
                tokenData.fileId(),
                tokenData.chunkIndex(),
                tokenData.chunkSize(),
                tokenData.maxParallelChunks(),
                tokenData.throughputLimit(),
                request.getInputStream(),
                checksumAlgorithm,
                checksumValue);

        uploadFileChunkUseCase.execute(input);

        return ResponseEntity
                .noContent()
                .build();

    }

    public ResponseEntity<StreamingResponseBody> downloadChunk(String chunkToken) {

        final var tokenData = tokenDecoder.decode(chunkToken);

        if (!tokenData.actor().equals(SecurityContext.getAuthenticatedUserId())
                || !"DOWNLOAD".equalsIgnoreCase(tokenData.type()))
            return ResponseEntity.status(403).build();

        final var input = new DownloadFileChunkInput(
                tokenData.fileId(),
                tokenData.chunkSize(),
                tokenData.chunkOffset(),
                tokenData.maxParallelChunks(),
                tokenData.throughputLimit());

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = downloadFileChunkUseCase.execute(input).data()) {
                byte[] buffer = new byte[16384];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            }
        };

        return ResponseEntity.ok().body(responseBody);

    }

}
