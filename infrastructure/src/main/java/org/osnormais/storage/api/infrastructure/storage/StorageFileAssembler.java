package org.osnormais.storage.api.infrastructure.storage;

@FunctionalInterface
public interface StorageFileAssembler {

    void assemble(StorageKey key, Long fileSize, Long chunkSize);

}
