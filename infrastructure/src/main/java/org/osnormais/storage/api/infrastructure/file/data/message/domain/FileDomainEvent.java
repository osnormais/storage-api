package org.osnormais.storage.api.infrastructure.file.data.message.domain;

import java.io.Serializable;
import java.util.UUID;

public record FileDomainEvent(UUID fileId) implements Serializable {

}
