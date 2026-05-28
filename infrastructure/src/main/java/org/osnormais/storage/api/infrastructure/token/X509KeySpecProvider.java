package org.osnormais.storage.api.infrastructure.token;

import static java.util.Objects.requireNonNull;

import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.springframework.stereotype.Component;

@Component
public class X509KeySpecProvider implements KeySpecProvider {

    private final byte[] encodedKey;

    public X509KeySpecProvider(final byte[] encodedKey) {
        this.encodedKey = Arrays.copyOf(requireNonNull(encodedKey), requireNonNull(encodedKey).length);
    }

    @Override
    public KeySpec provide() {
        return new X509EncodedKeySpec(encodedKey);
    }

}
