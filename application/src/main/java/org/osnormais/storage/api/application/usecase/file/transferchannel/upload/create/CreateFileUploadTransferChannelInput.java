package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create;

import java.util.UUID;

public record CreateFileUploadTransferChannelInput(
        UUID fileId,
        Long throughputBytesLimit,
        Long chunkBytesSize,
        Integer maxParallelChunks) {

}
