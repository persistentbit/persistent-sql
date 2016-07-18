package com.persistentbit.sql.references;

import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.objectmappers.*;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class RefObjectReaderWriter implements ObjectReader,ObjectWriter{
    private final Class refClass;
    private final Class cls;
    public RefObjectReaderWriter(Class refClass,Class cls){
        this.refClass = refClass;
        this.cls = cls;

    }

    @Override
    public void write(String fieldName, Object obj, ObjectWriter masterWriter, WritableRow result) {
        if(obj == null){
            return;
        }
        Ref ref = (Ref)obj;
        masterWriter.write(fieldName,((Ref) obj).getId().orElse(null),masterWriter,result);
    }

    @Override
    public Object read(String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
        Object id =  readerSupplier.apply(cls).read(name,readerSupplier,properties);
        if(id == null){
            return null;
        }
        try {
            return refClass.getConstructor(cls).newInstance(id);
        } catch (NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException  e) {
            throw new PersistSqlException("Error creating reference for " + id);
        }

    }

    static public void register(ObjectRowMapper mapper){
        register(mapper,IntRef.class,IntRefValue.class,Integer.class);
        register(mapper,LongRef.class,LongRefValue.class,Long.class);
        register(mapper,StringRef.class,StringRefValue.class,String.class);
    }

    static private <T extends RefId> void register(ObjectRowMapper mapper, Class<T> clsRefId,Class<? extends T> clsRefValue, Class idClass ){
        RefObjectReaderWriter id = new RefObjectReaderWriter(clsRefId,idClass);
        mapper.registerReader(clsRefId,id).registerWriter(clsRefId,id);
        mapper.registerReader(clsRefValue,id).registerWriter(clsRefValue,id);

    }
}
