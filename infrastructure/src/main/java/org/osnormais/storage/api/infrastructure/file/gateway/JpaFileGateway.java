package org.osnormais.storage.api.infrastructure.file.gateway;

import java.util.Optional;

import org.osnormais.storage.api.application.gateway.file.FileCommandGateway;
import org.osnormais.storage.api.application.gateway.file.FileQueryGateway;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.infrastructure.file.persistence.FileJpaEntity;
import org.osnormais.storage.api.infrastructure.file.persistence.FileJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaFileGateway implements FileCommandGateway, FileQueryGateway {

    private final FileJpaRepository fileRepository;

    public JpaFileGateway(final FileJpaRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Optional<File> findById(final FileId id) {
        return fileRepository
                .findById(id.getValue())
                .map(FileJpaEntity::toDomain);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public File create(final File file) {

        if (fileRepository.existsById(file.getId().getValue()))
            throw new IllegalStateException("File with id %s already exists".formatted(file.getId().getStringValue()));

        save(file);

        return file;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public File update(final File file) {

        if (!fileRepository.existsById(file.getId().getValue()))
            throw new IllegalStateException("File with id %s does not exist".formatted(file.getId().getStringValue()));

        save(file);

        return file;

    }

    private void save(final File file) {
        fileRepository.save(FileJpaEntity.fromDomain(file));
    }

}
