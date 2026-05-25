package org.osnormais.storage.api.application.port;

import org.osnormais.storage.api.domain.file.File;
import org.osnormais.storage.api.domain.file.valueobject.Checksum;

@FunctionalInterface
public interface FileFinalizer {

    Checksum finalize(File file, Long chunkSizeInBytes);

}
