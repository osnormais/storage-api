package org.osnormais.storage.api.domain.exception;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

public abstract class DomainException extends RuntimeException {

    private final List<DomainException.Error> errors;

    protected DomainException(
            final String message,
            final List<DomainException.Error> errors,
            final Throwable cause,
            final boolean verbose) {
        super(message, cause, enableSuppression(verbose), writableStackTrace(verbose));
        final List<DomainException.Error> causeErrors = mapCauseToErrorList(cause);
        this.errors = (causeErrors.isEmpty() && nonNullList(errors).isEmpty())
                ? List.of(DomainException.Error.with(message))
                : flat(List.of(causeErrors, errors));
    }

    public List<DomainException.Error> getErrors() {
        return List.copyOf(errors);
    }

    public String errorsToString() {
        return "[" + errors.stream()
                .map(DomainException.Error::message)
                .reduce((a, b) -> a + ", " + b)
                .orElse("No errors") + "]";
    }

    private static boolean enableSuppression(final boolean verbose) {
        return verbose ? false : true;
    }

    private static boolean writableStackTrace(final boolean verbose) {
        return verbose ? true : false;
    }

    private static List<DomainException.Error> mapCauseToErrorList(final Throwable cause) {

        if (cause instanceof DomainException domainException)
            return domainException.getErrors();

        return isNull(cause) ? List.of() : List.of(DomainException.Error.with(cause));
    }

    private static <T> List<T> flat(final List<List<T>> lists) {
        final List<T> result = new ArrayList<>();
        lists.forEach(list -> result.addAll(nonNullList(list)));
        return result;
    }

    private static <T> List<T> nonNullList(final List<T> list) {
        return isNull(list) ? List.of() : list;
    }

    public record Error(String message) {

        public static DomainException.Error with(final String message) {
            return new DomainException.Error(message);
        }

        public static DomainException.Error with(final Throwable cause) {
            return new DomainException.Error(
                    "Exception:[" + cause.getClass().getName() + "] Message:[" + cause.getMessage() + "]");
        }

    }

}