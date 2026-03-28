package org.osnormais.storage.api.application.port;

import java.io.InputStream;

import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

@FunctionalInterface
public interface ChunkReader {

    InputStream readChunk(
            FileId key,
            Long chunkOffset,
            Size chunkSize,
            ThroughputLimit throughputLimit);

}
