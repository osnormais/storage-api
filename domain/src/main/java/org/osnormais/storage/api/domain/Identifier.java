package org.osnormais.storage.api.domain;

import java.util.Objects;

import org.osnormais.storage.api.domain.validation.ValidationError;
import org.osnormais.storage.api.domain.validation.handler.ValidationHandler;

public abstract class Identifier<T> implements ValueObject {

    protected T id;

    protected Identifier(final T id) {
        this.id = id;
    }

    public abstract String getStringValue();

    public T getValue() {
        return id;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (Objects.isNull(this.id))
            handler.append(new ValidationError("'id' should not be null"));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
