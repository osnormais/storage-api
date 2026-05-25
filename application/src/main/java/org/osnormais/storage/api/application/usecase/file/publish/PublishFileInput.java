package org.osnormais.storage.api.application.usecase.file.publish;

import java.util.UUID;

public record PublishFileInput(UUID fileId, Long chunkSizeInBytes) {

}
