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
import org.osnormais.storage.api.domain.exception.FileAlreadyPublishedException;
import org.osnormais.storage.api.domain.exception.InvalidArgumentException;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.event.FilePublishFailedEvent;
import org.osnormais.storage.api.domain.file.event.FilePublishedEvent;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Publication;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class File extends AggregateRoot<FileId> implements DomainEventSource {

    private final Size size;
    private final Checksum checksum;
    private Optional<Publication> publication;

    private final Queue<DomainEvent<?>> events;

    private File(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final Optional<Publication> publication,
            final Queue<DomainEvent<?>> events) {
        super(id);
        this.size = size;
        this.checksum = checksum;
        this.publication = publication;

        this.events = Objects.isNull(events) ? new LinkedList<>() : new LinkedList<>(events);

        selfValidate();
    }

    public static File with(
            final FileId id,
            final Size size,
            final Checksum checksum,
            final Publication publication,
            final Queue<DomainEvent<?>> events) {
        return new File(
                id,
                size,
                checksum,
                Optional.ofNullable(publication),
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
                new LinkedList<>());
    }

    public File publicate(final Supplier<Checksum> checksumSupplier) {

        if (isNull(checksumSupplier))
            throw InvalidArgumentException.with("'checksumSupplier' should not be null");

        if (isPublished())
            throw FileAlreadyPublishedException.create(this);

        // if (hasOpenUploadChannel())
        // throw FileUploadInProgressException.create(this);

        final Checksum checksum = checksumSupplier.get();

        if (isNull(checksum))
            throw InvalidArgumentException.with("'checksum' should not be null");

        if (this.checksum.equals(checksum)) {

            this.publication = Optional.of(Publication.success());
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

    public Boolean isPublished() {
        return this.publication
                .map(Publication::status)
                .filter(status -> Publication.Status.SUCCESS.equals(status))
                .isPresent();
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

}
