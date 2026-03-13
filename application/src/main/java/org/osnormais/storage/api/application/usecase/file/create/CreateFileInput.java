package org.osnormais.storage.api.application.usecase.file.create;

import java.util.UUID;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;

public record CreateFileInput(
        UUID id,
        Long sizeInBytes,
        String checksumValue,
        Checksum.Algorithm checksumAlgorithm) {

}
