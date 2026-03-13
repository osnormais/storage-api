package org.osnormais.storage.api.application.usecase.file.create;

import java.util.UUID;

import org.osnormais.storage.api.domain.file.File;

public record CreateFileOutput(UUID id) {

    public static CreateFileOutput of(final File file) {
        return new CreateFileOutput(file.getId().getValue());
    }

}
