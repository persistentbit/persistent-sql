package com.persistentbit.sql.references;

import com.persistentbit.core.Immutable;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 8/06/2016
 */
@Immutable
public class RefId<R,ID> implements Ref<R,ID>{
    private final ID  id;

    public RefId(ID id) {
        this.id = id;
    }

    @Override
    public Optional<R> getValue() {
        return Optional.empty();
    }

    @Override
    public Optional<ID> getId() {
        return Optional.of(id);
    }

    @Override
    public Ref<R,ID> asIdRef() {
        return this;
    }
    @Override
    public  Ref<R,ID> asValueRef(Function<ID,R> resolver) {
        return new RefValue(id,getValue(resolver));
    }


    @Override
    public R getValue(Function<ID,R> resolver) {
        return resolver.apply(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Ref == false){
            return false;
        }
        if(this == obj){
            return true;
        }
        Ref r = (Ref)obj;
        return getId().equals(r.asIdRef().getId());
    }


    @Override
    public String toString() {
        return "RefId(" + id + ")";
    }
}
