package org.osnormais.storage.api.infrastructure.storage.filesystem;

import static java.util.Objects.requireNonNull;
import static org.osnormais.storage.api.infrastructure.commons.InputStreamUtils.bounded;
import static org.osnormais.storage.api.infrastructure.commons.InputStreamUtils.digestible;
import static org.osnormais.storage.api.infrastructure.commons.InputStreamUtils.throttled;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.infrastructure.commons.FileSystemUtils;
import org.osnormais.storage.api.infrastructure.commons.MessageDigestUtils;
import org.osnormais.storage.api.infrastructure.commons.StringUtils;
import org.osnormais.storage.api.infrastructure.exception.UnexpectedException;
import org.osnormais.storage.api.infrastructure.storage.StorageKey;
import org.osnormais.storage.api.infrastructure.storage.StorageService;

public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    public FileSystemStorageService(final Path rootLocation) {
        this.rootLocation = requireNonNull(rootLocation);
    }

    @Override
    public Checksum write(
            final StorageKey key,
            final InputStream inputStream,
            final Long sizeInBytes,
            final Long bytesPerSecondsWrittenRate,
            final Algorithm checksumAlgorithm) {

        final String fullKey = key.getFullKey();

        final Path storageLocation = rootLocation.resolve(fullKey);

        final MessageDigest digest = MessageDigestUtils.create(checksumAlgorithm);

        write(
                storageLocation,
                inputStream,
                sizeInBytes,
                bytesPerSecondsWrittenRate,
                digest);

        return new Checksum(checksumAlgorithm, StringUtils.toHexString(digest.digest()));

    }

    private static void write(
            final Path fileOutputPath,
            final InputStream inputStream,
            final Long sizeInBytes,
            final Long bytesPerSecondsWrittenRate,
            final MessageDigest digest) {

        try (final InputStream is = digestible(
                throttled(bounded(inputStream, sizeInBytes), bytesPerSecondsWrittenRate),
                digest)) {

            FileSystemUtils.write(fileOutputPath, is, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            throw UnexpectedException.with("Failed to write input stream", e);
        }

    }

}
