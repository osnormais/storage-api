package org.osnormais.storage.api.domain.file.event;

import java.time.Instant;
import java.util.Set;

import org.osnormais.storage.api.domain.Entity;
import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventEntity;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public class FileUploadTrasnferChannelCompletedEvent extends DomainEvent<FileId> {

    private static final Class<? extends Entity<?>> ENTITY_CLASS = File.class;
    private static final String SUB_RESOURCE = "upload-trasnfer-channel";
    private static final String ACTION = "completed";

    private FileUploadTrasnferChannelCompletedEvent(
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

    public static FileUploadTrasnferChannelCompletedEvent create(final File file) {
        return new FileUploadTrasnferChannelCompletedEvent(file, Instant.now(), Set.of(DomainEventEntity.of(file)));
    }

    public static String eventKey() {
        return DomainEvent.key(ENTITY_CLASS, SUB_RESOURCE, ACTION);
    }

}
