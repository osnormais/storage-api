package org.osnormais.storage.api.domain.file;

import java.util.UUID;

import org.osnormais.storage.api.domain.Identifier;

public class FileId extends Identifier<UUID> {

    public FileId(UUID id) {
        super(id);

    }

    @Override
    public String getStringValue() {

        return id.toString();
    }

}
