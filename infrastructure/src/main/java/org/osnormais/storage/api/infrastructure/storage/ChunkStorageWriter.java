package org.osnormais.storage.api.infrastructure.storage;

import java.io.InputStream;
import java.util.Objects;

import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

public class ChunkStorageWriter implements ChunkWriter {

    private final StorageWriter storageWriter;

    public ChunkStorageWriter(final StorageWriter storageWriter) {
        this.storageWriter = Objects.requireNonNull(storageWriter);
    }

    @Override
    public Checksum writeChunk(
            FileId key,
            Long chunkIndex,
            Size chunkSize,
            ThroughputLimit throughputLimit,
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
                chunkSize.bytes(),
                throughputLimit.bytesPerSecond(),
                checksumAlgorithm);

    }

}
