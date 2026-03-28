package org.osnormais.storage.api.application.exception;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

public class ApplicationException extends RuntimeException {

    private final List<ApplicationException.Error> errors;

    protected ApplicationException(
            final String message,
            final List<ApplicationException.Error> errors,
            final Throwable cause,
            final boolean verbose) {
        super(message, cause, enableSuppression(verbose), writableStackTrace(verbose));
        final List<ApplicationException.Error> causeErrors = mapCauseToErrorList(cause);
        this.errors = (causeErrors.isEmpty() && nonNullList(errors).isEmpty())
                ? List.of(ApplicationException.Error.with(message))
                : flat(List.of(causeErrors, errors));
    }

    public List<ApplicationException.Error> getErrors() {
        return List.copyOf(errors);
    }

    public String errorsToString() {
        return "[" + errors.stream()
                .map(ApplicationException.Error::message)
                .reduce((a, b) -> a + ", " + b)
                .orElse("No errors") + "]";
    }

    private static boolean enableSuppression(final boolean verbose) {
        return verbose ? false : true;
    }

    private static boolean writableStackTrace(final boolean verbose) {
        return verbose ? true : false;
    }

    private static List<ApplicationException.Error> mapCauseToErrorList(final Throwable cause) {

        if (cause instanceof ApplicationException applicationException)
            return applicationException.getErrors();

        return isNull(cause) ? List.of() : List.of(ApplicationException.Error.with(cause));
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

        public static ApplicationException.Error with(final String message) {
            return new ApplicationException.Error(message);
        }

        public static ApplicationException.Error with(final Throwable cause) {
            return new ApplicationException.Error(
                    "Exception:[" + cause.getClass().getName() + "] Message:[" + cause.getMessage() + "]");
        }

    }
}
