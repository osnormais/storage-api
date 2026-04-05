package org.osnormais.storage.api.domain;

import java.util.Objects;

import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public abstract class Identifier<T> implements ValueObject {

    protected T value;

    protected Identifier() {
    }

    protected Identifier(final T value) {
        this.value = value;
    }

    public abstract String getStringValue();

    public T getValue() {
        return value;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (Objects.isNull(this.value))
            handler.append(new ValidationError("'Identifier.value' should not be null"));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Identifier<T> other = (Identifier<T>) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
