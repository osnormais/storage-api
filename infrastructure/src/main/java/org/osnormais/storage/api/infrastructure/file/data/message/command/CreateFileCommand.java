package org.osnormais.storage.api.infrastructure.file.data.message.command;

import java.io.Serializable;
import java.util.UUID;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public record CreateFileCommand(
        UUID id,
        Long sizeInBytes,
        String checksumValue,
        Checksum.Algorithm checksumAlgorithm) implements Serializable {

}
