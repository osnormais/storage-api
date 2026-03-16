package org.osnormais.storage.api.domain.file.event;

import java.time.Instant;
import java.util.Set;

import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventEntity;
import org.osnormais.storage.api.domain.file.File;

public class FileUploadTrasnferChannelCompletedEvent extends DomainEvent {

    private FileUploadTrasnferChannelCompletedEvent(
            Instant occurredAt,
            Set<DomainEventEntity> relatedEntities) {
        super(
                File.class,
                "upload-trasnfer-channel",
                "completed",
                occurredAt,
                relatedEntities);
    }

    public static FileUploadTrasnferChannelCompletedEvent create(final File file) {
        return new FileUploadTrasnferChannelCompletedEvent(Instant.now(), Set.of(DomainEventEntity.of(file)));
    }

}
