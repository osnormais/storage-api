package org.osnormais.storage.api.application.usecase.file.transferchannel.download.create;

import java.util.UUID;

public record CreateFileDownloadTransferChannelInput(
        UUID fileId,
        Long throughputBytesLimit,
        Long chunkBytesSize,
        Integer maxParallelChunks) {

}
