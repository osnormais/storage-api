package org.osnormais.storage.api.infrastructure.file.data.message;

import java.io.Serializable;
import java.util.UUID;

public record FileUploadTransferChannelCompletedMessage(UUID fileId) implements Serializable {

}
