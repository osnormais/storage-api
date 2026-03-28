package org.osnormais.storage.api.infrastructure.configuration.storage;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.port.ChunkReader;
import org.osnormais.storage.api.application.port.ChunkWriter;
import org.osnormais.storage.api.infrastructure.storage.ChunkStorageWriter;
import org.osnormais.storage.api.infrastructure.storage.StorageChunkReader;
import org.osnormais.storage.api.infrastructure.storage.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChunkIOConfig {

    private final StorageService storageService;

    public ChunkIOConfig(final StorageService storageService) {
        this.storageService = requireNonNull(storageService);
    }

    @Bean
    ChunkWriter chunkWriter() {
        return new ChunkStorageWriter(storageService);
    }

    @Bean
    ChunkReader chunkReader() {
        return new StorageChunkReader(storageService);
    }

}