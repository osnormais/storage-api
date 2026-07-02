package org.osnormais.storage.api.infrastructure.token;

import com.auth0.jwt.algorithms.Algorithm;

@FunctionalInterface
public interface AlgorithmProvider {

    Algorithm provide();

}
