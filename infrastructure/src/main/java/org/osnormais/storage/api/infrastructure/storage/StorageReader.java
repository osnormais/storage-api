package org.osnormais.storage.api.infrastructure.storage;

import java.io.InputStream;

@FunctionalInterface
public interface StorageReader {

    InputStream read(
            StorageKey key,
            Long offset,
            Long sizeInBytes,
            Long bytesPerSecondsReadRate);

}
