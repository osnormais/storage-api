package org.osnormais.storage.api.application.usecase.file.transferchannel.download.create;

import java.util.UUID;

public record CreateFileDownloadTransferChannelOutput(
        UUID fileId,
        Long totalChunks,
        Long chunkBytesSize,
        Long lastChunkBytesSize) {

}
