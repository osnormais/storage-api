package org.osnormais.storage.api.infrastructure.commons.transaction;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.osnormais.storage.api.infrastructure.exception.ExceptionWrapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class TransactionalSupplier<T> implements TransactionCallback<T> {

    private final Supplier<T> transactionResultSupplier;

    TransactionalSupplier(final Supplier<T> resultSupplier) {
        this.transactionResultSupplier = requireNonNull(resultSupplier);
    }

    public static <T> TransactionalSupplier<T> of(final Supplier<T> resultSupplier) {
        return new TransactionalSupplier<>(resultSupplier);
    }

    @Override
    public T doInTransaction(TransactionStatus status) {

        try {
            return transactionResultSupplier.get();
        } catch (Exception e) {
            throw ExceptionWrapper.wrap(e);
        }

    }
}
