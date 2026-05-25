package org.osnormais.storage.api.infrastructure.storage;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;

import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.domain.file.FileId;

public class StorageChunkReader implements ChunkReader {

    private final StorageReader storageReader;

    public StorageChunkReader(final StorageReader storageReader) {
        this.storageReader = requireNonNull(storageReader);
    }

    @Override
    public InputStream readChunk(
            final FileId key,
            final Long chunkOffset,
            final Long chunkSizeInBytes,
            final Long throughputLimitInBytesPerSecond) {

        final StorageKey storageKey = StorageKey.create(key.getStringValue()).subKey("data");

        return storageReader
                .read(
                        storageKey,
                        chunkOffset,
                        chunkSizeInBytes,
                        throughputLimitInBytesPerSecond);

    }

}
