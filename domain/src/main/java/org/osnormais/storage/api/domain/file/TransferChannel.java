package org.osnormais.storage.api.domain.file;

import static java.util.Objects.isNull;

import org.osnormais.storage.api.domain.Entity;
import org.osnormais.storage.api.domain.exception.ValidationException;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;
import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.Notification;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public class TransferChannel extends Entity<TransferChannelId> {

    private TransferChannelStatus status;
    private final ThroughputLimit throughputLimit;
    private final ChunkSpecification chunkSpecification;

    private TransferChannel(
            final TransferChannelId id,
            final TransferChannelStatus status,
            final ThroughputLimit throughputLimit,
            final ChunkSpecification chunkSpecification) {
        super(id);
        this.status = status;
        this.throughputLimit = throughputLimit;
        this.chunkSpecification = chunkSpecification;

        selfValidate();
    }

    public static TransferChannel with(
            final TransferChannelId id,
            final TransferChannelStatus status,
            final ThroughputLimit throughputLimit,
            final ChunkSpecification chunkSpecification) {
        return new TransferChannel(id, status, throughputLimit, chunkSpecification);
    }

    public static TransferChannel create(
            final ThroughputLimit throughputLimit,
            final ChunkSpecification chunkSpecification) {
        return new TransferChannel(
                TransferChannelId.unique(),
                TransferChannelStatus.OPENED,
                throughputLimit,
                chunkSpecification);
    }

    @Override
    public void validate(final ValidationHandler handler) {

        if (isNull(status))
            handler.append(ValidationError.with("'status' should not be null"));

        if (isNull(throughputLimit))
            handler.append(ValidationError.with("'throughputLimit' should not be null"));
        else
            throughputLimit.validate(handler);

        if (isNull(chunkSpecification))
            handler.append(ValidationError.with("'chunkSpecification' should not be null"));
        else
            chunkSpecification.validate(handler);

    }

    public Boolean isOpen() {
        return TransferChannelStatus.OPENED.equals(this.status);
    }

    public TransferChannel close() {
        this.status = TransferChannelStatus.CLOSED;
        return this;
    }

    private void selfValidate() {
        final Notification notification = Notification.create();
        validate(notification);
        if (notification.hasErrors())
            throw ValidationException.with("'TransferChannel' validation failed", notification);
    }

    public TransferChannelStatus getStatus() {
        return status;
    }

    public ThroughputLimit getThroughputLimit() {
        return throughputLimit;
    }

    public ChunkSpecification getChunkSpecification() {
        return chunkSpecification;
    }

    @Override
    public String toString() {
        return "TransferChannel [id=" + getId().getStringValue()
                + "status=" + status
                + ", throughputLimit=" + throughputLimit
                + ", chunkSpecification=" + chunkSpecification + "]";
    }

}
