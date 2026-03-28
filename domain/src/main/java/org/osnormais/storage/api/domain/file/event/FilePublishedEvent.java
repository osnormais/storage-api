package org.osnormais.storage.api.domain.file.event;

import java.time.Instant;
import java.util.Set;

import org.osnormais.storage.api.domain.Entity;
import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventEntity;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class FilePublishedEvent extends DomainEvent<FileId> {

    private static final Class<? extends Entity<?>> ENTITY_CLASS = File.class;
    private static final String ACTION = "published";

    private FilePublishedEvent(
            final File file,
            final Instant occurredAt,
            final Set<DomainEventEntity> relatedEntities) {
        super(
                file,
                null,
                ACTION,
                occurredAt,
                relatedEntities);
    }

    public static FilePublishedEvent create(final File file) {
        return new FilePublishedEvent(file, Instant.now(), Set.of(DomainEventEntity.of(file)));
    }

    public static String eventKey() {
        return DomainEvent.key(ENTITY_CLASS, null, ACTION);
    }

}
