package com.persistentbit.sql.references;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class IntRefValue<R>  extends IntRef<R> {
    private final R value;

    public IntRefValue(Integer id, R value) {
        super(id);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<R> getValue() {
        return Optional.of(value);
    }

    @Override
    public  IntRef<R> asValueRef(Function<Integer,R> resolver) {
        return this;
    }

    @Override
    public R getValue(Function<Integer,R> resolver) {
        return value;
    }


    @Override
    public String toString() {
        return "IntRefValue(" + value + ")";
    }
}
