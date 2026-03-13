package org.osnormais.storage.api.application.usecase.file.chunk.upload;

import java.io.InputStream;
import java.util.UUID;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public record UploadFileChunkInput(
        UUID fileId,
        Long chunkIndex,
        InputStream chunkData,
        Checksum.Algorithm checksumAlgorithm,
        String checksumValue) {

}
