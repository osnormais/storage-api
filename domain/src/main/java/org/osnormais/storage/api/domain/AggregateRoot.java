package org.osnormais.storage.api.domain;

public abstract class AggregateRoot<I extends Identifier<?>> extends Entity<I> {

    protected AggregateRoot(final I id) {
        super(id);
    }

}
