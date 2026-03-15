package org.osnormais.storage.api.infrastructure.commons;

import static java.util.Objects.isNull;

import java.security.MessageDigest;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.infrastructure.exception.InvalidArgumentException;
import org.osnormais.storage.api.infrastructure.exception.UnexpectedException;

public final class MessageDigestUtils {

    private MessageDigestUtils() {
    }

    public static MessageDigest create(final Checksum.Algorithm algorithm) {

        if (isNull(algorithm))
            throw InvalidArgumentException.with("Algorithm cannot be null.");

        try {

            final String alg = switch (algorithm) {
                case MD5 -> "MD5";
                case CRC_32 -> "CRC32";
                case SHA_256 -> "SHA-256";
            };

            return MessageDigest.getInstance(alg);
        } catch (Exception e) {
            throw UnexpectedException.with("Failed to create message digest for algorithm: " + algorithm.name(), e);
        }
    }

}
