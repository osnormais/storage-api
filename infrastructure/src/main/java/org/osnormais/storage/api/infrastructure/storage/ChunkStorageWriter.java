package org.osnormais.storage.api.infrastructure.storage;

import java.io.InputStream;
import java.util.Objects;

import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public class ChunkStorageWriter implements ChunkWriter {

    private final StorageWriter storageWriter;

    public ChunkStorageWriter(final StorageWriter storageWriter) {
        this.storageWriter = Objects.requireNonNull(storageWriter);
    }

    @Override
    public Checksum writeChunk(
            FileId key,
            Long chunkIndex,
            Long chunkSizeInBytes,
            Long throughputLimitInBytesPerSecond,
            Checksum.Algorithm checksumAlgorithm,
            InputStream inputStream) {

        final StorageKey chunkStorageKey = StorageKey
                .create(key.getStringValue())
                .subKey(
                        "upload",
                        "chunks",
                        chunkIndex.toString());

        return storageWriter.write(
                chunkStorageKey,
                inputStream,
                chunkSizeInBytes,
                throughputLimitInBytesPerSecond,
                checksumAlgorithm);

    }

}
