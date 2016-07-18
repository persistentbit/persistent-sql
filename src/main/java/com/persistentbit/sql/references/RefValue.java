package com.persistentbit.sql.references;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 8/06/2016
 */
public class RefValue<R,ID> extends RefId<R,ID>{

    private final R value;

    public RefValue(ID id, R value) {
        super(id);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<R> getValue() {
        return Optional.of(value);
    }

    @Override
    public  Ref<R,ID> asValueRef(Function<ID,R> resolver) {
        return this;
    }

    @Override
    public R getValue(Function<ID,R> resolver) {
        return value;
    }


    @Override
    public String toString() {
        return "RefValue(" + value + ")";
    }
}
