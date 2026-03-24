package org.osnormais.storage.api.infrastructure.storage;

import static java.util.Objects.requireNonNull;

import org.osnormais.storage.api.application.port.FileFinalizer;
import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;

public class StorageFileFinalizer implements FileFinalizer {

    private final StorageFileAssembler assembler;
    private final StorageChecksumProvider checksumProvider;

    public StorageFileFinalizer(
            final StorageFileAssembler assembler,
            final StorageChecksumProvider checksumProvider) {
        this.assembler = requireNonNull(assembler);
        this.checksumProvider = requireNonNull(checksumProvider);
    }

    @Override
    public Checksum finalize(final File file) {

        final StorageKey storageKey = StorageKey.of(file.getId().getStringValue());
        final Size fileSize = file.getSize();
        final Checksum.Algorithm checksumAlgorithm = file.getChecksum().algorithm();
        final Size chunkSize = file
                .getUploadChannel()
                .map(TransferChannel::getChunkSpecification)
                .map(chunkSpecification -> chunkSpecification.effectiveChunkSize(fileSize))
                .orElseThrow(() -> new IllegalArgumentException("Chunk size not found for file: " + file.getId()));

        assembler.assemble(storageKey, fileSize.bytes(), chunkSize.bytes());

        return checksumProvider.calculateChecksum(storageKey, checksumAlgorithm);

    }

}
