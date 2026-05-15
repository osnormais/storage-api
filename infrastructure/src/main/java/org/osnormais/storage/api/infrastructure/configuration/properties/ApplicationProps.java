package org.osnormais.storage.api.infrastructure.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProps(String name, TransferChannel transferChannel) {

    public record TransferChannel(Specs upload, Specs download) {

        public record Specs(Long chunkSizeBytes, Integer chunkMaxParallel, Long chunkMaxBytesPerSecond) {
        }

    }

}
