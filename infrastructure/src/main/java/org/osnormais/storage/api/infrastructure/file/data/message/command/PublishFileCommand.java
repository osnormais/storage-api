package org.osnormais.storage.api.infrastructure.file.data.message.command;

import java.io.Serializable;
import java.util.UUID;

public record PublishFileCommand(UUID id) implements Serializable {

}
