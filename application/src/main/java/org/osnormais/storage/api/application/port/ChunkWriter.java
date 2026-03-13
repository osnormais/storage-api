package org.osnormais.storage.api.application.port;

import java.io.InputStream;

import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

@FunctionalInterface
public interface ChunkWriter {

    Checksum writeChunk(
            FileId key,
            Long chunkIndex,
            Size chunkSize,
            ThroughputLimit throughputLimit,
            Checksum.Algorithm checksumAlgorithm,
            InputStream inputStream);

}