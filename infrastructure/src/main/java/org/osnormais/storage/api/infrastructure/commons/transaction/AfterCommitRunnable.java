package org.osnormais.storage.api.infrastructure.commons.transaction;

import org.springframework.transaction.support.TransactionSynchronization;

public final class AfterCommitRunnable implements TransactionSynchronization {

    private final Runnable runnable;

    private AfterCommitRunnable(final Runnable runnable) {
        this.runnable = runnable;
    }

    public static AfterCommitRunnable of(final Runnable runnable) {
        return new AfterCommitRunnable(runnable);
    }

    @Override
    public void afterCommit() {
        runnable.run();
    }

}