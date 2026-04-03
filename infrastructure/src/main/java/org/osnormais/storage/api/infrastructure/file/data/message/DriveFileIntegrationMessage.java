package org.osnormais.storage.api.infrastructure.file.data.message;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public record DriveFileIntegrationMessage(
        UUID id,
        UUID creatorId,
        UUID ownerId,
        UUID folderId,
        Checksum.Algorithm checksumAlgorithm,
        String checksumValue,
        Long size,
        String name,
        String contentType,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) implements Serializable {

}
