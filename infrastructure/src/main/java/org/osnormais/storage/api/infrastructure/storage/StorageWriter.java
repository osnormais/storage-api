package org.osnormais.storage.api.infrastructure.storage;

import java.io.InputStream;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

@FunctionalInterface
public interface StorageWriter {

    Checksum write(
            StorageKey key,
            InputStream inputStream,
            Long sizeInBytes,
            Long bytesPerSecondsWrittenRate,
            Checksum.Algorithm checksumAlgorithm);

}
