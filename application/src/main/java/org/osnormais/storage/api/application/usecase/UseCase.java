package org.osnormais.storage.api.application.usecase;

public abstract class UseCase<I, O> {

    public abstract O execute(I input);

}
