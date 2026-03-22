package org.osnormais.storage.api.infrastructure.storage;

public interface StorageService extends
        StorageWriter,
        StorageFileAssembler,
        StorageChecksumProvider {

}
