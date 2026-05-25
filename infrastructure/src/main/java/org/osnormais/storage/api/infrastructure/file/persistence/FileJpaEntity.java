package org.osnormais.storage.api.infrastructure.file.persistence;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.osnormais.storage.api.domain.file.valueobject.Checksum.Algorithm;
import org.osnormais.storage.api.domain.file.valueobject.Publication;
import org.osnormais.storage.api.domain.file.valueobject.Publication.Status;
import org.osnormais.storage.api.domain.file.valueobject.Size;

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

    @Column(length = 400)
    private String publicationErrorMessage;

    public FileJpaEntity() {
    }

    private FileJpaEntity(
            UUID id,
            Long sizeInBytes,
            Algorithm checksumAlgorithm,
            String checksumValue,
            Instant publishedAt,
            Status publicationStatus,
            String publicationErrorMessage) {
        this.id = id;
        this.sizeInBytes = sizeInBytes;
        this.checksumAlgorithm = checksumAlgorithm;
        this.checksumValue = checksumValue;
        this.publishedAt = publishedAt;
        this.publicationStatus = publicationStatus;
        this.publicationErrorMessage = publicationErrorMessage;
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