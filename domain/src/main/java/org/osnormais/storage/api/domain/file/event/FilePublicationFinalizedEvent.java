package org.osnormais.storage.api.domain.file.event;

import java.time.Instant;
import java.util.Set;

import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventEntity;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class FilePublicationFinalizedEvent extends DomainEvent<FileId> {

    private static final Class<File> ENTITY_CLASS = File.class;
    private static final String SUB_RESOURCE = "publication";
    private static final String ACTION = "finalized";

    FilePublicationFinalizedEvent() {
    }

    private FilePublicationFinalizedEvent(
            final File file,
            final Instant occurredAt,
            final Set<DomainEventEntity> relatedEntities) {
        super(
                file,
                SUB_RESOURCE,
                ACTION,
                occurredAt,
                relatedEntities);
    }

    public static FilePublicationFinalizedEvent create(final File file) {
        return new FilePublicationFinalizedEvent(file, Instant.now(), Set.of(DomainEventEntity.of(file)));
    }

    public static String eventKey() {
        return DomainEvent.key(ENTITY_CLASS, SUB_RESOURCE, ACTION);
    }

}
