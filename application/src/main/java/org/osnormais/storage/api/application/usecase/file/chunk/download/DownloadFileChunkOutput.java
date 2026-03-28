package org.osnormais.storage.api.application.usecase.file.chunk.download;

import java.io.InputStream;

public record DownloadFileChunkOutput(InputStream data) {

}
