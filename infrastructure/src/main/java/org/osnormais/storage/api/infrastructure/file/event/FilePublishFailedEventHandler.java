package org.osnormais.storage.api.infrastructure.file.event;

import org.osnormais.storage.api.domain.event.DomainEventHandler;
import org.osnormais.storage.api.domain.file.event.FilePublishFailedEvent;

public class FilePublishFailedEventHandler extends DomainEventHandler<FilePublishFailedEvent> {

    FilePublishFailedEventHandler() {
        super(FilePublishFailedEvent.eventKey());
    }

    @Override
    public void handle(final FilePublishFailedEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

}
