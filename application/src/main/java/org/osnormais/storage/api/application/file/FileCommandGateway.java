package org.osnormais.storage.api.application.file;

import org.osnormais.storage.api.domain.file.File;

public interface FileCommandGateway {

    void create(File file);

}
