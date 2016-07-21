package com.persistentbit.sql.references;


import java.util.Optional;
import java.util.function.Function;

/**
 * A Ref always has a non null Id and kan have a none null Value.<br>
 *
 * @param <T>
 * @param <ID>
 */
public interface Ref<T,ID> {
    Optional<T> getValue();
    ID getId();

    Ref<T,ID>   asIdRef();
    Ref<T,ID>   asValueRef(T value);
    //Class<ID>   getIdClass();



}