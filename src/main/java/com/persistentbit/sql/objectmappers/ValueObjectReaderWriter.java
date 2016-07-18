package com.persistentbit.sql.objectmappers;

import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class ValueObjectReaderWriter implements ObjectReader,ObjectWriter{
    private final Class cls;


    public ValueObjectReaderWriter(Class cls){
        this.cls = cls;
    }

    @Override
    public void write(String fieldName, Object obj, ObjectWriter masterWriter, WritableRow result) {
        result.write(fieldName,obj);
    }

    @Override
    public Object read(String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
        return properties.read(cls,name);
    }
}
