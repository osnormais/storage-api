package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.retrieve;

import java.util.UUID;

import org.osnormais.storage.api.domain.file.TransferChannelStatus;

public record RetrieveFileUploadTransferChannelOutput(
        UUID fileId,
        TransferChannelStatus status,
        Long throughputBytesLimit,
        Long totalChunks,
        Long chunkBytesSize,
        Long lastChunkBytesSize,
        Integer maxParallelChunks) {

}

