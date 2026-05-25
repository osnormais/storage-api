package org.osnormais.storage.api.application.usecase.file.chunk.download;

import java.util.UUID;

public record DownloadFileChunkInput(
        UUID fileId,
        Long chunkSize,
        Long chunkOffset,
        Integer maxParallelChunks,
        Long throughputLimit) {

}
