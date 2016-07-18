package com.persistentbit.sql.references;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class StringRefValue<R>  extends StringRef<R> {
    private final R value;

    public StringRefValue(String id, R value) {
        super(id);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<R> getValue() {
        return Optional.of(value);
    }

    @Override
    public  StringRefValue<R> asValueRef(Function<String,R> resolver) {
        return this;
    }

    @Override
    public R getValue(Function<String,R> resolver) {
        return value;
    }


    @Override
    public String toString() {
        return "StringRefValue(" + value + ")";
    }
}