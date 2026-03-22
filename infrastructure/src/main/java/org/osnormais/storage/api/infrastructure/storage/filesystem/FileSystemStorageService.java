package org.osnormais.storage.api.infrastructure.storage.filesystem;

import static java.util.Objects.requireNonNull;
import static org.osnormais.storage.api.infrastructure.commons.InputStreamUtils.bounded;
import static org.osnormais.storage.api.infrastructure.commons.InputStreamUtils.digestible;
import static org.osnormais.storage.api.infrastructure.commons.InputStreamUtils.throttled;

import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Set;
import java.util.stream.Collectors;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.infrastructure.commons.FileSystemUtils;
import org.osnormais.storage.api.infrastructure.commons.InputStreamUtils;
import org.osnormais.storage.api.infrastructure.commons.MessageDigestUtils;
import org.osnormais.storage.api.infrastructure.commons.SequentialIterator;
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

    @Override
    public void assemble(final StorageKey key, final Long fileSize) {

        final StorageKey finalFileKey = key.subKey("data");

        final Set<SequentialIterator.Item<Path>> items = FileSystemUtils
                .listFiles(toPath(key))
                .stream()
                .map(chunkPath -> SequentialIterator.Item
                        .of(chunkPath, Long.valueOf(chunkPath.getFileName().toString())))
                .collect(Collectors.toSet());

        final SequentialIterator<Path> iterator = SequentialIterator.of(items);

        try (final FileChannel outputChannel = FileSystemUtils.openChannel(
                toPath(finalFileKey),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            while (iterator.hasNext()) {

                final Path chunkPath = iterator.next();

                if (!FileSystemUtils.exists(chunkPath))
                    continue;

                try (final FileChannel inputChannel = FileSystemUtils.openChannel(chunkPath, StandardOpenOption.READ)) {
                    FileSystemUtils.append(outputChannel, inputChannel);
                }

                FileSystemUtils.delete(chunkPath);
            }

        } catch (Exception e) {
            throw UnexpectedException.with("Failed to assemble file for key: " + key.getFullKey(), e);
        }

    }

    @Override
    public Checksum calculateChecksum(final StorageKey key, final Checksum.Algorithm checksumAlgorithm) {

        final StorageKey fileDataKey = key.subKey("data");
        final Path fileDataPath = toPath(fileDataKey);

        if (!FileSystemUtils.exists(fileDataPath))
            throw UnexpectedException.with("File data not found for key: " + key.getFullKey());

        final MessageDigest digest = MessageDigestUtils.create(checksumAlgorithm);

        try (final FileChannel fileChannel = FileSystemUtils.openChannel(fileDataPath, StandardOpenOption.READ)) {

            final InputStream digestibleInputStream = InputStreamUtils
                    .digestible(
                            FileSystemUtils.read(fileChannel, 0L),
                            digest);

            byte[] buffer = new byte[8192];
            while (digestibleInputStream.read(buffer) != -1) {
                // Reading the stream to calculate the digest
            }

        } catch (Exception e) {
            throw UnexpectedException.with("Failed to calculate checksum for key: " + key.getFullKey(), e);
        }

        return new Checksum(checksumAlgorithm, StringUtils.toHexString(digest.digest()));

    }

    private Path toPath(final StorageKey storageKey) {
        return rootLocation.resolve(storageKey.getFullKey());
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
