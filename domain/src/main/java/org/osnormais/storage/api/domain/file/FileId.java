package org.osnormais.storage.api.domain.file;

import java.util.UUID;

import org.osnormais.storage.api.domain.Identifier;

public class FileId extends Identifier<UUID> {

    public FileId(UUID id) {
        super(id);

    public static FileId of(final UUID id) {
        return new FileId(id);
    }

    @Override
    public String getStringValue() {

        return id.toString();
    }

}
