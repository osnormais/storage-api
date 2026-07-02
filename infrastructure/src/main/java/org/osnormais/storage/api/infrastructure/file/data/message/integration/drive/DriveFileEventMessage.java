package org.osnormais.storage.api.infrastructure.file.data.message.integration.drive;

import java.io.Serializable;
import java.util.UUID;

public record DriveFileEventMessage(UUID id) implements Serializable {

}
