package org.osnormais.storage.api.infrastructure.configuration.storage;

import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.infrastructure.storage.StorageChecksumProvider;
import org.osnormais.storage.api.infrastructure.storage.StorageFileAssembler;
import org.osnormais.storage.api.infrastructure.storage.StorageFileFinalizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageFileFinalizerConfig {

    @Bean
    FileFinalizer fileFinalizer(
            final StorageFileAssembler assembler,
            final StorageChecksumProvider checksumProvider) {
        return new StorageFileFinalizer(assembler, checksumProvider);
    }

}
