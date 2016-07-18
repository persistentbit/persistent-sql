package com.persistentbit.sql.references;


import java.util.Optional;
import java.util.function.Function;

public interface Ref<T,ID> {
    Optional<T> getValue();
    Optional<ID> getId();

    Ref<T,ID> asIdRef();
    Ref<T,ID> asValueRef(Function<ID,T> resolver);

    T getValue(Function<ID,T> resolver);


}