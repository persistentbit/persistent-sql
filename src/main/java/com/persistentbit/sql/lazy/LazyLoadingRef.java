package com.persistentbit.sql.lazy;

import com.persistentbit.sql.objectmappers.ObjectReader;
import com.persistentbit.sql.objectmappers.ObjectWriter;
import com.persistentbit.sql.objectmappers.ReadableRow;
import com.persistentbit.sql.objectmappers.WritableRow;
import com.persistentbit.sql.references.Ref;
import com.persistentbit.sql.references.RefId;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * User: petermuys
 * Date: 21/07/16
 * Time: 11:39
 */
public class LazyLoadingRef<T,ID> implements Ref<T,ID>{
    private final Ref<T,ID> id;
    private Supplier<T> valueSupplier;
    private T value;

    public LazyLoadingRef(Ref<T,ID> id, Supplier<T> valueSupplier){
        this.id = id;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public synchronized  Optional<T> getValue() {
        if(value == null){
            value = valueSupplier.get();
            valueSupplier = null;
        }
        return Optional.of(value);
    }

    @Override
    public ID getId() {
        return id.getId();
    }

    @Override
    public Ref<T, ID> asIdRef() {
        return id;
    }

    @Override
    public Ref<T, ID> asValueRef(T value) {
        return this;
    }


    static public ObjectReader  createObjectReader() {
        return new ObjectReader() {
            @Override
            public Object read(Type typeToRead, String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                return readerSupplier.apply(RefId.class).read(typeToRead,name,readerSupplier,properties);
            }
        };
    }
    static public ObjectWriter createObjectWriter() {
        return new ObjectWriter() {
            @Override
            public void write(String fieldName, Object obj, ObjectWriter masterWriter, WritableRow result) {
                if(obj == null){
                    return;
                }
                LazyLoadingRef llref = (LazyLoadingRef)obj;
                masterWriter.write(fieldName,llref.id,masterWriter,result);
            }
        };
    }

    @Override
    public String toString() {
        return "LazyLoadingRef(" + getId() + ", " + getValue().map(p->p.toString()).orElse("?") + ")";
    }
}
