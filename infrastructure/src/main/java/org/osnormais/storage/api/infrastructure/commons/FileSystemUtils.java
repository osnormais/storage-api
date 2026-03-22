package org.osnormais.storage.api.infrastructure.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osnormais.storage.api.infrastructure.exception.InvalidArgumentException;
import org.osnormais.storage.api.infrastructure.exception.UnexpectedException;

public final class FileSystemUtils {

    private FileSystemUtils() {
    }

    public static Boolean exists(final Path filePath) {
        return Files.exists(filePath);
    }

    public static void write(
            final Path outputLocation,
            final InputStream content,
            final CopyOption... options) {

        if (content == null)
            throw InvalidArgumentException.with("Failed to write empty file.");

        final Path destinationFile = outputLocation.normalize().toAbsolutePath();

        try (InputStream inputStream = content) {
            Files.createDirectories(destinationFile.getParent());
            Files.copy(inputStream, destinationFile, options);

        } catch (FileAlreadyExistsException e) {
            throw UnexpectedException.with("File already exists: " + destinationFile.toString(), e);
        } catch (IOException e) {
            throw UnexpectedException.with("Failed to write file.", e);
        }

    }

    public static InputStream read(final FileChannel channel, Long offset) {
        try {
            channel.position(offset);
            return Channels.newInputStream(channel);
        } catch (IOException e) {
            throw UnexpectedException.with("Failed to read file.", e);
        }
    }

    public static void delete(final Path filePath) {
        try {

            if (Files.notExists(filePath))
                return;

            try (Stream<Path> walk = Files.walk(filePath)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }

        } catch (IOException e) {
            throw UnexpectedException.with("Failed to delete file: " + filePath.toString(), e);
        }
    }

    public static FileChannel openChannel(final Path path, final StandardOpenOption... options) {
        try {
            return FileChannel.open(path, options);
        } catch (IOException e) {
            throw UnexpectedException.with("Failed to open file channel: " + path.toString(), e);
        }
    }

    public static void append(
            final FileChannel targetChannel,
            final FileChannel sourceChannel) {

        try {

            Long inputSize = sourceChannel.size();
            Long transferredBytes = 0L;
            while (transferredBytes < inputSize) {
                transferredBytes += sourceChannel.transferTo(
                        transferredBytes,
                        inputSize - transferredBytes,
                        targetChannel);
            }

        } catch (Exception e) {
            throw UnexpectedException.with("Failed to append file channels.", e);
        }

    }

    public static Set<Path> listFiles(final Path directory) {
        try (Stream<Path> walk = Files.walk(directory)) {
            return walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw UnexpectedException.with("Failed to list files in directory: " + directory.toString(), e);
        }
    }

}
