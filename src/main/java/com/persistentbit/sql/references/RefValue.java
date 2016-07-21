package com.persistentbit.sql.references;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 8/06/2016
 */
public class RefValue<R,ID> implements Ref<R,ID>{

    private final RefId<R,ID> refId;
    private final R value;

    public RefValue(RefId<R,ID> refId, R value) {
        this.refId = Objects.requireNonNull(refId);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<R> getValue() {
        return Optional.of(value);
    }

    @Override
    public ID getId() {
        return refId.getId();
    }

    @Override
    public Ref<R, ID> asIdRef() {
        return refId;
    }

    @Override
    public Ref<R, ID> asValueRef(R value) {
        return new RefValue<R, ID>(refId,value);
    }


    @Override
    public String toString() {
        return "RefValue(" + value + ")";
    }
}
