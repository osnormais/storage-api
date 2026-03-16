package org.osnormais.storage.api.domain.file;

import static java.util.Objects.isNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

import org.osnormais.storage.api.domain.AggregateRoot;
import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventSource;
import org.osnormais.storage.api.domain.exception.DomainException;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.exception.UploadTransferChannelAlreadyOpennedException;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.event.FileUploadTrasnferChannelCompletedEvent;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.TransferChannel;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class File extends AggregateRoot<FileId> implements DomainEventSource {

    private final Size size;
    private final Checksum checksum;

    private Optional<TransferChannel> uploadChannel;
    private Optional<TransferChannel> downloadChannel;

    private final Queue<DomainEvent<?>> events;

    private File(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final Optional<TransferChannel> uploadChannel,
            final Optional<TransferChannel> downloadChannel,
            final Queue<DomainEvent<?>> events) {
        super(id);
        this.size = size;
        this.checksum = checksum;
        this.uploadChannel = uploadChannel;
        this.downloadChannel = downloadChannel;

        this.events = Objects.isNull(events) ? new LinkedList<>() : new LinkedList<>(events);

        selfValidate();
    }

    public static File with(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final TransferChannel uploadChannel,
            final TransferChannel downloadChannel,
            final Queue<DomainEvent<?>> events) {
        return new File(
                id,
                size,
                checksum,
                Optional.ofNullable(uploadChannel),
                Optional.ofNullable(uploadChannel),
                events);
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

    @Override
    public Optional<DomainEvent<?>> nextEvent() {
        return Optional.ofNullable(this.events.poll());
    }

    public static File create(
            final FileId id,
            final Size size,
            final Checksum checksum) {
        return new File(
                id,
                size,
                checksum,
                Optional.empty(),
                Optional.empty(),
                new LinkedList<>());
    }

    public File openUploadChannel(final TransferChannel transferChannel) {

        if (isNull(transferChannel))
            throw InvalidArgumentException.with(DomainException.Error.with("'transferChannel' should not be null"));

        if (this.uploadChannel.isPresent())
            throw UploadTransferChannelAlreadyOpennedException.create();

        this.uploadChannel = Optional.of(transferChannel);

        return this;

    }

    public File completeUploadChannel() {

        if (this.uploadChannel.isEmpty())
            return this;

        this.uploadChannel = Optional.empty();
        events.add(FileUploadTrasnferChannelCompletedEvent.create(this));

        return this;

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

    public Optional<TransferChannel> getUploadChannel() {
        return uploadChannel;
    }

    public Optional<TransferChannel> getDownloadChannel() {
        return downloadChannel;
    }

}
