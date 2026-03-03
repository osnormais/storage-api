package org.osnormais.storage.api.domain.file;

import java.util.Objects;

import org.osnormais.storage.api.domain.AggregateRoot;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class File extends AggregateRoot<FileId> {

    private final long size;
    private final Checksum checksum;

    public File(FileId id, long size, Checksum checksum) {
        super(id);
        this.size = size;
        this.checksum = checksum;
    }

    @Override
    public void validate(ValidationHandler handler) {
        if (Objects.isNull(getId()))
            handler.append(new ValidationError("id cant be null"));

        if (size < 0)
            handler.append(new ValidationError("size must be greater than or equal to 0"));

        if (Objects.isNull(checksum))
            handler.append(new ValidationError("checksum cant be null"));
        else
            checksum.validate(handler);

    }

    public long getSize() {
        return size;
    }

    public Checksum getChecksum() {
        return checksum;
    }
}
