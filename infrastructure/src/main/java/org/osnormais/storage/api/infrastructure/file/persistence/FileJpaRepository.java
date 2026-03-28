package org.osnormais.storage.api.infrastructure.file.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, UUID> {

}
