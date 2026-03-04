package org.osnormais.storage.api.domain.file;

public record TransferChannel(
        ThroughputLimit throughputLimit,
        ChunkSpecification chunkSpecification) {

}