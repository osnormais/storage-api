package org.osnormais.storage.api.infrastructure.configuration.storage;

import java.nio.file.Path;

import org.osnormais.storage.api.infrastructure.storage.StorageService;
import org.osnormais.storage.api.infrastructure.storage.filesystem.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "application.vendor.storage", havingValue = "fs")
public class FileSystemStorageConfig {

    private final Path storageRootPath;

    public FileSystemStorageConfig(@Value("${storage.fs.root-path}") Path storageRootPath) {
        this.storageRootPath = storageRootPath;
    }

    @Bean
    StorageService fileSystemStorageService() {
        return new FileSystemStorageService(storageRootPath);
    }

}
