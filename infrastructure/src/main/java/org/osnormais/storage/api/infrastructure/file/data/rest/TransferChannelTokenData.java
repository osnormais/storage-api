package org.osnormais.storage.api.infrastructure.file.data.rest;

import java.time.Instant;
import java.util.UUID;

public record TransferChannelTokenData(
        UUID actor,
        Instant expiresAt,
        UUID fileId,
        String type,
        Integer maxParallelChunks,
        Long throughputLimit,
        Long chunkIndex,
        Long chunkOffset,
        Long chunkSize) {

}
