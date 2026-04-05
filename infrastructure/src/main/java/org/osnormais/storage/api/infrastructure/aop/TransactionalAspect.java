package org.osnormais.storage.api.infrastructure.aop;

import static java.util.Objects.requireNonNull;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.osnormais.storage.api.infrastructure.commons.transaction.TransactionalSupplier;
import org.osnormais.storage.api.infrastructure.exception.ExceptionWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Aspect
@Component
@Order(value = 1)
public class TransactionalAspect {

    private final TransactionTemplate transactionTemplate;

    public TransactionalAspect(final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = requireNonNull(transactionTemplate);
    }

    @Around("@annotation(org.osnormais.storage.api.application.commons.annotation.Transactional)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return transactionTemplate.execute(TransactionalSupplier.of(() -> {
                try {
                    return joinPoint.proceed();
                } catch (final Throwable e) {
                    throw ExceptionWrapper.wrap(e);
                }
            }));
        } catch (final ExceptionWrapper e) {
            throw e.getCause();
        }
    }

}
