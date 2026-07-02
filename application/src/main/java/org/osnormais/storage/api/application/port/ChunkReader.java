package org.osnormais.storage.api.application.port;

import java.io.InputStream;

import org.osnormais.storage.api.domain.file.FileId;

@FunctionalInterface
public interface ChunkReader {

    InputStream readChunk(
            FileId key,
            Long chunkOffset,
            Long chunkSizeInBytes,
            Long throughputLimitInBytesPerSecond);

}
