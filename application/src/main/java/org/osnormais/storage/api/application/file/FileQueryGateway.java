package org.osnormais.storage.api.application.file;

import java.util.Optional;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.FileId;

public interface FileQueryGateway {

    Optional<File> findById(FileId id);

}