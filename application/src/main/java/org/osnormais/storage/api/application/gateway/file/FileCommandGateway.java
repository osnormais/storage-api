package org.osnormais.storage.api.application.gateway.file;

import org.osnormais.storage.api.domain.file.File;

public interface FileCommandGateway {

    File create(File file);

    File update(File file);

}
