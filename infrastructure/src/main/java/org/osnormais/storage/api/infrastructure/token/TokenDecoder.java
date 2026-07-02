package org.osnormais.storage.api.infrastructure.token;

@FunctionalInterface
public interface TokenDecoder<T> {

    T decode(final String token);

}
