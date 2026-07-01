package org.osnormais.storage.api.infrastructure.file.data.message.integration.storage;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Publication;

public record StorageFileIntegrationMessage(
        UUID id,
        Long sizeInBytes,
        Checksum.Algorithm checksumAlgorithm,
        String checksumValue,
        Instant publishedAt,
        String publicationStatus,
        String publicationError) implements Serializable {

    public static StorageFileIntegrationMessage from(final File file) {
        return new StorageFileIntegrationMessage(
                file.getId().getValue(),
                file.getSize().bytes(),
                file.getChecksum().algorithm(),
                file.getChecksum().value(),
                file.getPublication().map(Publication::publishedAt).orElse(null),
                file.getPublication().map(Publication::status).map(Publication.Status::name).orElse(null),
                file.getPublication().flatMap(Publication::error).map(Publication.Error::message).orElse(null));
    }

}
