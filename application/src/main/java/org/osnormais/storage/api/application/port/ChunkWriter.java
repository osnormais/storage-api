package org.osnormais.storage.api.application.port;

import java.io.InputStream;

import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;

@FunctionalInterface
public interface ChunkWriter {

    Checksum writeChunk(
            FileId key,
            Long chunkIndex,
            Long chunkSizeInBytes,
            Long throughputLimitInBytesPerSecond,
            Checksum.Algorithm checksumAlgorithm,
            InputStream inputStream);

}