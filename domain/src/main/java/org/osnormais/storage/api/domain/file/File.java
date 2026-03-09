package org.osnormais.storage.api.domain.file;

import static java.util.Objects.isNull;

import java.util.Optional;

import org.osnormais.storage.api.domain.AggregateRoot;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class File extends AggregateRoot<FileId> {

    private final Size size;
    private final Checksum checksum;

    private Optional<TransferChannel> uploadChannel;
    private Optional<TransferChannel> downloadChannel;

    private File(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final Optional<TransferChannel> uploadChannel,
            final Optional<TransferChannel> downloadChannel) {
        super(id);
        this.size = size;
        this.checksum = checksum;
        this.uploadChannel = uploadChannel;
        this.downloadChannel = downloadChannel;

        selfValidate();
    }

    public static File with(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final TransferChannel uploadChannel,
            final TransferChannel downloadChannel) {
        return new File(
                id,
                size,
                checksum,
                Optional.ofNullable(uploadChannel),
                Optional.ofNullable(uploadChannel));
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

        uploadChannel.ifPresent(uploadChannel -> uploadChannel.validate(handler));
        downloadChannel.ifPresent(downloadChannel -> downloadChannel.validate(handler));

    }

    public static File create(
            final FileId id,
            final Size size,
            final Checksum checksum) {
        return new File(
                id,
                size,
                checksum,
                null,
                null);
    }

    private void selfValidate() {
        final Notification notification = Notification.create();
        validate(notification);
        if (notification.hasErrors())
            throw ValidationException.with("'File' validation failed", notification);
    }

    public Size getSize() {
        return size;
    }

    public Checksum getChecksum() {
        return checksum;
    }

}
