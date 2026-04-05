package org.osnormais.storage.api.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.osnormais.storage.api.infrastructure.commons.transaction.AfterCommitRunnable;
import org.osnormais.storage.api.infrastructure.exception.ExceptionWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@Order(value = 0)
public class OutboxRelayAspect {

    @Around("execution(void org.osnormais.storage.api.domain.event.DomainEventDispatcher.dispatch(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        if (TransactionSynchronizationManager.isActualTransactionActive())
            scheduleDispatch(joinPoint);
        else
            return joinPoint.proceed();

        return null;
    }

    private void scheduleDispatch(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            TransactionSynchronizationManager
                    .registerSynchronization(
                            AfterCommitRunnable.of(
                                    () -> {
                                        try {
                                            joinPoint.proceed();
                                        } catch (final Throwable e) {
                                            throw ExceptionWrapper.wrap(e);
                                        }
                                    }));
        } catch (final ExceptionWrapper e) {
            throw e.getCause();
        }

    }
}
