package org.osnormais.storage.api.domain.file;

import java.util.UUID;

import org.osnormais.storage.api.domain.Identifier;

public class TransferChannelId extends Identifier<UUID> {

    private TransferChannelId(UUID id) {
        super(id);
    }

    public static TransferChannelId unique() {
        return new TransferChannelId(UUID.randomUUID());
    }

    public static TransferChannelId of(final UUID id) {
        return new TransferChannelId(id);
    }

    @Override
    public String getStringValue() {
        return value.toString();
    }

}