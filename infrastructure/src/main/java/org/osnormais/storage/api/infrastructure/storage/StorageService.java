package org.osnormais.storage.api.infrastructure.storage;

public interface StorageService extends
        StorageReader,
        StorageWriter,
        StorageFileAssembler,
        StorageChecksumProvider {

}
