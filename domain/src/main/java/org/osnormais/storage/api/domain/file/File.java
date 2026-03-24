package org.osnormais.storage.api.domain.file;

import static java.util.Objects.isNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

import org.osnormais.storage.api.domain.AggregateRoot;
import org.osnormais.storage.api.domain.event.DomainEvent;
import org.osnormais.storage.api.domain.event.DomainEventSource;
import org.osnormais.storage.api.domain.exception.DomainException;
import org.osnormais.storage.api.domain.exception.FileAlreadyPublishedException;
import org.osnormais.storage.api.domain.exception.FileUploadInProgressException;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.exception.UploadTransferChannelAlreadyOpennedException;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.event.FilePublishFailedEvent;
import org.osnormais.storage.api.domain.file.event.FilePublishedEvent;
import org.osnormais.storage.api.domain.file.event.FileUploadTransferChannelCompletedEvent;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.Publication;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class File extends AggregateRoot<FileId> implements DomainEventSource {

    private final Size size;
    private final Checksum checksum;
    private Optional<Publication> publication;

    private Optional<TransferChannel> uploadChannel;
    private Optional<TransferChannel> downloadChannel;

    private final Queue<DomainEvent<?>> events;

    private File(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final Optional<Publication> publication,
            final Optional<TransferChannel> uploadChannel,
            final Optional<TransferChannel> downloadChannel,
            final Queue<DomainEvent<?>> events) {
        super(id);
        this.size = size;
        this.checksum = checksum;
        this.publication = publication;
        this.uploadChannel = uploadChannel;
        this.downloadChannel = downloadChannel;

        this.events = Objects.isNull(events) ? new LinkedList<>() : new LinkedList<>(events);

        selfValidate();
    }

    public static File with(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final Publication publication,
            final TransferChannel uploadChannel,
            final TransferChannel downloadChannel,
            final Queue<DomainEvent<?>> events) {
        return new File(
                id,
                size,
                checksum,
                Optional.ofNullable(publication),
                Optional.ofNullable(uploadChannel),
                Optional.ofNullable(downloadChannel),
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

        publication.ifPresent(publication -> publication.validate(handler));
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
                Optional.empty(),
                new LinkedList<>());
    }

    public TransferChannel openUploadChannel(final ThroughputLimit throughputLimit,
            final ChunkSpecification chunkSpecification) {

        if (isNull(throughputLimit))
            throw InvalidArgumentException.with(DomainException.Error.with("'throughputLimit' should not be null"));

        if (isNull(chunkSpecification))
            throw InvalidArgumentException.with(DomainException.Error.with("'chunkSpecification' should not be null"));

        if (isPublished())
            throw FileAlreadyPublishedException.create(this);

        if (hasOpenUploadChannel())
            throw UploadTransferChannelAlreadyOpennedException.create();

        final TransferChannel transferChannel = TransferChannel.create(throughputLimit, chunkSpecification);
        this.uploadChannel = Optional.of(transferChannel);
        return transferChannel;

    }

    public File completeUploadChannel() {

        if (isPublished())
            throw FileAlreadyPublishedException.create(this);

        if (!hasOpenUploadChannel())
            return this;

        this.uploadChannel.ifPresent(TransferChannel::close);
        events.add(FileUploadTransferChannelCompletedEvent.create(this));

        return this;

    }

    public File publicate(final Supplier<Checksum> checksumSupplier) {

        if (isPublished())
            throw FileAlreadyPublishedException.create(this);

        if (hasOpenUploadChannel())
            throw FileUploadInProgressException.create(this);

        final Checksum checksum = checksumSupplier.get();

        if (isNull(checksum))
            throw InvalidArgumentException.with(DomainException.Error.with("'checksum' should not be null"));

        if (this.checksum.equals(checksum)) {

            this.publication = Optional.of(Publication.ok());
            events.add(FilePublishedEvent.create(this));

        } else {

            final Publication.Error error = Publication.Error
                    .of("File integrity compromised during finalization. Expected checksum: %s, actual checksum: %s"
                            .formatted(this.checksum, checksum));

            this.publication = Optional.of(Publication.error(error));
            events.add(FilePublishFailedEvent.create(this));

        }

        return this;

    }

    private Boolean isPublished() {
        return this.publication
                .map(Publication::status)
                .filter(status -> Publication.Status.OK.equals(status))
                .isPresent();
    }

    private Boolean hasOpenUploadChannel() {
        return uploadChannel
                .map(TransferChannel::isOpen)
                .orElse(false);
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

    public Optional<Publication> getPublication() {
        return publication;
    }

    public Optional<TransferChannel> getUploadChannel() {
        return uploadChannel;
    }

    public Optional<TransferChannel> getDownloadChannel() {
        return downloadChannel;
    }

}
