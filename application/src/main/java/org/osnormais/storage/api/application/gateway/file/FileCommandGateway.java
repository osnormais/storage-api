package org.osnormais.storage.api.application.gateway.file;

import org.osnormais.storage.api.domain.file.File;

public interface FileCommandGateway {

    void create(File file);

    File update(File file);

}
