package org.osnormais.storage.api.infrastructure.storage;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;

import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

public class StorageChunkReader implements ChunkReader {

    private final StorageReader storageReader;

    public StorageChunkReader(final StorageReader storageReader) {
        this.storageReader = requireNonNull(storageReader);
    }

    @Override
    public InputStream readChunk(
            final FileId key,
            final Long chunkOffset,
            final Size chunkSize,
            final ThroughputLimit throughputLimit) {

        final StorageKey storageKey = StorageKey.create(key.getStringValue()).subKey("data");

        return storageReader
                .read(
                        storageKey,
                        chunkOffset,
                        chunkSize.bytes(),
                        throughputLimit.bytesPerSecond());

    }

}
