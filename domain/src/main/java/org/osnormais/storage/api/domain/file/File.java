package org.osnormais.storage.api.domain.file;

import static java.util.Objects.isNull;

import org.osnormais.storage.api.domain.AggregateRoot;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class File extends AggregateRoot<FileId> {

    private final Size size;
    private final Checksum checksum;

    public File(
            final FileId id,
            final Size size,
            final Checksum checksum) {
        super(id);
        this.size = size;
        this.checksum = checksum;
    }

    @Override
    public void validate(final ValidationHandler handler) {

        if (isNull(getId()))
            handler.append(new ValidationError("id cant be null"));
        else
            getId().validate(handler);

        if (isNull(size))
            handler.append(new ValidationError("size cant be null"));
        else
            size.validate(handler);

        if (isNull(checksum))
            handler.append(new ValidationError("checksum cant be null"));
        else
            checksum.validate(handler);

    }

    public Size getSize() {
        return size;
    }

    public Checksum getChecksum() {
        return checksum;
    }

}
