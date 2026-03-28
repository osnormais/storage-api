package org.osnormais.storage.api.infrastructure.file.persistence;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.TransferChannel;
import org.osnormais.storage.api.domain.file.TransferChannelId;
import org.osnormais.storage.api.domain.file.TransferChannelStatus;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.domain.file.valueobject.ChunkSpecification;
import org.osnormais.storage.api.domain.file.valueobject.ParallelChunkLimit;
import org.osnormais.storage.api.domain.file.valueobject.Publication;
import org.osnormais.storage.api.domain.file.valueobject.Publication.Status;
import org.osnormais.storage.api.domain.file.valueobject.Size;
import org.osnormais.storage.api.domain.file.valueobject.ThroughputLimit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "files")
@Entity
public class FileJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Long sizeInBytes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Checksum.Algorithm checksumAlgorithm;

    @Column(nullable = false)
    private String checksumValue;

    private Instant publishedAt;

    @Enumerated(EnumType.STRING)
    private Publication.Status publicationStatus;

    private String publicationErrorMessage;

    private UUID uploadChannelId;

    @Enumerated(EnumType.STRING)
    private TransferChannelStatus uploadChannelStatus;

    private Long uploadChannelThroughputLimitInBytesPerSecond;

    private Long uploadChannelChunkSpecificationSizeInBytes;

    private Integer uploadChannelChunkSpecificationMaxParallelChunkLimit;

    private UUID downloadChannelId;

    @Enumerated(EnumType.STRING)
    private TransferChannelStatus downloadChannelStatus;

    private Long downloadChannelThroughputLimitInBytesPerSecond;

    private Long downloadChannelChunkSpecificationSizeInBytes;

    private Integer downloadChannelChunkSpecificationMaxParallelChunkLimit;

    public FileJpaEntity() {
    }

    private FileJpaEntity(
            UUID id,
            Long sizeInBytes,
            Algorithm checksumAlgorithm,
            String checksumValue,
            Instant publishedAt,
            Status publicationStatus,
            String publicationErrorMessage,
            UUID uploadChannelId,
            TransferChannelStatus uploadChannelStatus,
            Long uploadChannelThroughputLimitInBytesPerSecond,
            Long uploadChannelChunkSpecificationSizeInBytes,
            Integer uploadChannelChunkSpecificationMaxParallelChunkLimit,
            UUID downloadChannelId,
            TransferChannelStatus downloadChannelStatus,
            Long downloadChannelThroughputLimitInBytesPerSecond,
            Long downloadChannelChunkSpecificationSizeInBytes,
            Integer downloadChannelChunkSpecificationMaxParallelChunkLimit) {
        this.id = id;
        this.sizeInBytes = sizeInBytes;
        this.checksumAlgorithm = checksumAlgorithm;
        this.checksumValue = checksumValue;
        this.publishedAt = publishedAt;
        this.publicationStatus = publicationStatus;
        this.publicationErrorMessage = publicationErrorMessage;
        this.uploadChannelId = uploadChannelId;
        this.uploadChannelStatus = uploadChannelStatus;
        this.uploadChannelThroughputLimitInBytesPerSecond = uploadChannelThroughputLimitInBytesPerSecond;
        this.uploadChannelChunkSpecificationSizeInBytes = uploadChannelChunkSpecificationSizeInBytes;
        this.uploadChannelChunkSpecificationMaxParallelChunkLimit = uploadChannelChunkSpecificationMaxParallelChunkLimit;
        this.downloadChannelId = downloadChannelId;
        this.downloadChannelStatus = downloadChannelStatus;
        this.downloadChannelThroughputLimitInBytesPerSecond = downloadChannelThroughputLimitInBytesPerSecond;
        this.downloadChannelChunkSpecificationSizeInBytes = downloadChannelChunkSpecificationSizeInBytes;
        this.downloadChannelChunkSpecificationMaxParallelChunkLimit = downloadChannelChunkSpecificationMaxParallelChunkLimit;
    }

    public static FileJpaEntity fromDomain(final File file) {

        return new FileJpaEntity(
                file.getId().getValue(),
                file.getSize().bytes(),
                file.getChecksum().algorithm(),
                file.getChecksum().value(),
                file.getPublication().map(Publication::publishedAt).orElse(null),
                file.getPublication().map(Publication::status).orElse(null),
                file.getPublication()
                        .map(Publication::error)
                        .orElse(Optional.empty())
                        .map(Publication.Error::message)
                        .orElse(null),
                file.getUploadChannel()
                        .map(TransferChannel::getId)
                        .map(TransferChannelId::getValue)
                        .orElse(null),
                file.getUploadChannel()
                        .map(TransferChannel::getStatus)
                        .orElse(null),
                file.getUploadChannel()
                        .map(TransferChannel::getThroughputLimit)
                        .map(ThroughputLimit::bytesPerSecond)
                        .orElse(null),
                file.getUploadChannel()
                        .map(TransferChannel::getChunkSpecification)
                        .map(ChunkSpecification::size)
                        .map(Size::bytes)
                        .orElse(null),
                file.getUploadChannel()
                        .map(TransferChannel::getChunkSpecification)
                        .map(ChunkSpecification::maxParallel)
                        .map(ParallelChunkLimit::value)
                        .orElse(null),
                file.getDownloadChannel()
                        .map(TransferChannel::getId)
                        .map(TransferChannelId::getValue)
                        .orElse(null),
                file.getDownloadChannel()
                        .map(TransferChannel::getStatus)
                        .orElse(null),
                file.getDownloadChannel()
                        .map(TransferChannel::getThroughputLimit)
                        .map(ThroughputLimit::bytesPerSecond)
                        .orElse(null),
                file.getDownloadChannel()
                        .map(TransferChannel::getChunkSpecification)
                        .map(ChunkSpecification::size)
                        .map(Size::bytes)
                        .orElse(null),
                file.getDownloadChannel()
                        .map(TransferChannel::getChunkSpecification)
                        .map(ChunkSpecification::maxParallel)
                        .map(ParallelChunkLimit::value)
                        .orElse(null));

    }

    public File toDomain() {

        return File.with(
                FileId.of(id),
                Size.of(sizeInBytes),
                Checksum.of(checksumAlgorithm, checksumValue),
                isNull(publishedAt) ? null
                        : new Publication(
                                publishedAt,
                                publicationStatus,
                                Optional.ofNullable(isNull(publicationErrorMessage) ? null
                                        : Publication.Error.of(publicationErrorMessage))),
                isNull(uploadChannelId) ? null
                        : TransferChannel.with(
                                TransferChannelId.of(uploadChannelId),
                                uploadChannelStatus,
                                ThroughputLimit.create(uploadChannelThroughputLimitInBytesPerSecond),
                                ChunkSpecification.create(Size.of(uploadChannelChunkSpecificationSizeInBytes),
                                        ParallelChunkLimit.of(uploadChannelChunkSpecificationMaxParallelChunkLimit))),
                isNull(downloadChannelId) ? null
                        : TransferChannel.with(
                                TransferChannelId.of(downloadChannelId),
                                downloadChannelStatus,
                                ThroughputLimit.create(downloadChannelThroughputLimitInBytesPerSecond),
                                ChunkSpecification.create(Size.of(downloadChannelChunkSpecificationSizeInBytes),
                                        ParallelChunkLimit.of(downloadChannelChunkSpecificationMaxParallelChunkLimit))),
                null);

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public Checksum.Algorithm getChecksumAlgorithm() {
        return checksumAlgorithm;
    }

    public void setChecksumAlgorithm(Checksum.Algorithm checksumAlgorithm) {
        this.checksumAlgorithm = checksumAlgorithm;
    }

    public String getChecksumValue() {
        return checksumValue;
    }

    public void setChecksumValue(String checksumValue) {
        this.checksumValue = checksumValue;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Publication.Status getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(Publication.Status publicationStatus) {
        this.publicationStatus = publicationStatus;
    }

    public String getPublicationErrorMessage() {
        return publicationErrorMessage;
    }

    public void setPublicationErrorMessage(String publicationErrorMessage) {
        this.publicationErrorMessage = publicationErrorMessage;
    }

    public UUID getUploadChannelId() {
        return uploadChannelId;
    }

    public void setUploadChannelId(UUID uploadChannelId) {
        this.uploadChannelId = uploadChannelId;
    }

    public TransferChannelStatus getUploadChannelStatus() {
        return uploadChannelStatus;
    }

    public void setUploadChannelStatus(TransferChannelStatus uploadChannelStatus) {
        this.uploadChannelStatus = uploadChannelStatus;
    }

    public Long getUploadChannelThroughputLimitInBytesPerSecond() {
        return uploadChannelThroughputLimitInBytesPerSecond;
    }

    public void setUploadChannelThroughputLimitInBytesPerSecond(Long uploadChannelThroughputLimitInBytesPerSecond) {
        this.uploadChannelThroughputLimitInBytesPerSecond = uploadChannelThroughputLimitInBytesPerSecond;
    }

    public Long getUploadChannelChunkSpecificationSizeInBytes() {
        return uploadChannelChunkSpecificationSizeInBytes;
    }

    public void setUploadChannelChunkSpecificationSizeInBytes(Long uploadChannelChunkSpecificationSizeInBytes) {
        this.uploadChannelChunkSpecificationSizeInBytes = uploadChannelChunkSpecificationSizeInBytes;
    }

    public Integer getUploadChannelChunkSpecificationMaxParallelChunkLimit() {
        return uploadChannelChunkSpecificationMaxParallelChunkLimit;
    }

    public void setUploadChannelChunkSpecificationMaxParallelChunkLimit(
            Integer uploadChannelChunkSpecificationMaxParallelChunkLimit) {
        this.uploadChannelChunkSpecificationMaxParallelChunkLimit = uploadChannelChunkSpecificationMaxParallelChunkLimit;
    }

    public UUID getDownloadChannelId() {
        return downloadChannelId;
    }

    public void setDownloadChannelId(UUID downloadChannelId) {
        this.downloadChannelId = downloadChannelId;
    }

    public TransferChannelStatus getDownloadChannelStatus() {
        return downloadChannelStatus;
    }

    public void setDownloadChannelStatus(TransferChannelStatus downloadChannelStatus) {
        this.downloadChannelStatus = downloadChannelStatus;
    }

    public Long getDownloadChannelThroughputLimitInBytesPerSecond() {
        return downloadChannelThroughputLimitInBytesPerSecond;
    }

    public void setDownloadChannelThroughputLimitInBytesPerSecond(Long downloadChannelThroughputLimitInBytesPerSecond) {
        this.downloadChannelThroughputLimitInBytesPerSecond = downloadChannelThroughputLimitInBytesPerSecond;
    }

    public Long getDownloadChannelChunkSpecificationSizeInBytes() {
        return downloadChannelChunkSpecificationSizeInBytes;
    }

    public void setDownloadChannelChunkSpecificationSizeInBytes(Long downloadChannelChunkSpecificationSizeInBytes) {
        this.downloadChannelChunkSpecificationSizeInBytes = downloadChannelChunkSpecificationSizeInBytes;
    }

    public Integer getDownloadChannelChunkSpecificationMaxParallelChunkLimit() {
        return downloadChannelChunkSpecificationMaxParallelChunkLimit;
    }

    public void setDownloadChannelChunkSpecificationMaxParallelChunkLimit(
            Integer downloadChannelChunkSpecificationMaxParallelChunkLimit) {
        this.downloadChannelChunkSpecificationMaxParallelChunkLimit = downloadChannelChunkSpecificationMaxParallelChunkLimit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileJpaEntity other = (FileJpaEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}