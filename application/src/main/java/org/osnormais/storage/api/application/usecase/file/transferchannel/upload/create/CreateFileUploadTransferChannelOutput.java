package org.osnormais.storage.api.application.usecase.file.transferchannel.upload.create;

import java.util.UUID;

public record CreateFileUploadTransferChannelOutput(
        UUID fileId,
        Long totalChunks,
        Long chunkBytesSize,
        Long lastChunkBytesSize) {

}
