package com.persistentbit.sql.references;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class LongRefValue<R> extends LongRef<R>  {
    private final R value;

    public LongRefValue(Long id, R value) {
        super(id);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<R> getValue() {
        return Optional.of(value);
    }

    @Override
    public  LongRef<R> asValueRef(Function<Long,R> resolver) {
        return this;
    }

    @Override
    public R getValue(Function<Long,R> resolver) {
        return value;
    }


    @Override
    public String toString() {
        return "LongRefValue(" + value + ")";
    }
}
