package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve;

import java.util.UUID;

public record RetrieveFileUploadTransferChannelOutput(
        UUID fileId,
        Long throughputBytesLimit,
        Long totalChunks,
        Long chunkBytesSize,
        Long lastChunkBytesSize,
        Integer maxParallelChunks) {

}

