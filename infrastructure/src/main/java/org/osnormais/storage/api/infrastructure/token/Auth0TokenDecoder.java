package org.osnormais.storage.api.infrastructure.token;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

import org.osnormais.storage.api.infrastructure.file.data.rest.TransferChannelTokenData;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class Auth0TokenDecoder implements TokenDecoder<TransferChannelTokenData> {

    private final Algorithm algorithm;

    public Auth0TokenDecoder(final AlgorithmProvider algorithmProvider) {
        this.algorithm = requireNonNull(algorithmProvider.provide());
    }

    @Override
    public TransferChannelTokenData decode(final String token) {

        final var decodedJWT = JWT
                .require(algorithm)
                .build()
                .verify(token);

        return new TransferChannelTokenData(
                UUID.fromString(decodedJWT.getClaim("actor").asString()),
                decodedJWT.getExpiresAt().toInstant(),
                UUID.fromString(decodedJWT.getClaim("file").asString()),
                decodedJWT.getClaim("type").asString(),
                decodedJWT.getClaim("maxParallelChunks").asInt(),
                decodedJWT.getClaim("throughputLimit").asLong(),
                decodedJWT.getClaim("chunkIndex").asLong(),
                decodedJWT.getClaim("chunkOffset").asLong(),
                decodedJWT.getClaim("chunkSize").asLong());

    }

}
