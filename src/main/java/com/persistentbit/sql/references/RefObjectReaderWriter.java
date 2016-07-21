package com.persistentbit.sql.references;

import com.persistentbit.core.utils.ReflectionUtils;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.objectmappers.ObjectReader;
import com.persistentbit.sql.objectmappers.ObjectWriter;
import com.persistentbit.sql.objectmappers.ReadableRow;
import com.persistentbit.sql.objectmappers.WritableRow;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * User: petermuys
 * Date: 21/07/16
 * Time: 11:14
 */
public class RefObjectReaderWriter implements ObjectReader,ObjectWriter {

    @Override
    public void write(String fieldName, Object obj, ObjectWriter masterWriter, WritableRow result) {
        Ref value = (Ref)obj;
        if(value == null){
            return;
        }
        masterWriter.write(fieldName,value.getId(),masterWriter,result);
    }

    @Override
    public Object read(Type typeToRead, String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
        if(typeToRead instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType)typeToRead;
            Type[] types = pt.getActualTypeArguments();
            //Class cls1 = ReflectionUtils.classFromType(types[0]);
            Class clsID = ReflectionUtils.classFromType(types[1]);
            Object id = readerSupplier.apply(clsID).read(clsID,name,readerSupplier,properties);
            if(id == null){
                return null;
            }
            return new RefId(id);
        }
        throw new PersistSqlException("Wrong reference type: " + typeToRead + ", " + typeToRead.getClass());
    }
}
