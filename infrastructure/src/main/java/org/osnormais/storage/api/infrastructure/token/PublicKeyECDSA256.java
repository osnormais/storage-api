package org.osnormais.storage.api.infrastructure.token;

import static java.util.Objects.requireNonNull;

import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.KeySpec;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.auth0.jwt.algorithms.Algorithm;

@Component
@ConditionalOnProperty(name = "application.token.signature.algorithm", havingValue = "ECDSA256")
public class PublicKeyECDSA256 implements AlgorithmProvider {

    private final KeySpec keySpec;

    PublicKeyECDSA256(final KeySpecProvider keySpecProvider) {
        this.keySpec = requireNonNull(keySpecProvider).provide();
    }

    @Override
    public Algorithm provide() {

        try {
            final KeyFactory kf = KeyFactory.getInstance("EC");
            final ECPublicKey publicKey = (ECPublicKey) kf.generatePublic(keySpec);
            return Algorithm.ECDSA256(publicKey);
        } catch (Throwable e) {
            throw new RuntimeException(e); // TODO: Handle this properly
        }

    }

}
