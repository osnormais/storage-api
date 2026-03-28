package org.osnormais.storage.api.infrastructure.storage;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

@FunctionalInterface
public interface StorageChecksumProvider {

    Checksum calculateChecksum(StorageKey key, Checksum.Algorithm algorithm);

}
