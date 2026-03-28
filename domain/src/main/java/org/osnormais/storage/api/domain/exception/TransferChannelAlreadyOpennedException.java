package org.osnormais.storage.api.domain.exception;

import java.util.List;

public class TransferChannelAlreadyOpennedException extends SilentDomainException {

    private static final String MESSAGE = "Transfer channel already open";
    private static final String ERROR = MESSAGE + ", please close the current channel before opening a new one";

    private TransferChannelAlreadyOpennedException() {
        super(MESSAGE, List.of(Error.with(ERROR)));
    }

    public static TransferChannelAlreadyOpennedException create() {
        return new TransferChannelAlreadyOpennedException();
    }

}
