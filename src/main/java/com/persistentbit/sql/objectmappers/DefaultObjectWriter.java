package com.persistentbit.sql.objectmappers;

import com.persistentbit.core.Tuple2;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.utils.ImTools;
import com.persistentbit.sql.statement.annotations.DbIgnore;
import com.persistentbit.sql.statement.annotations.DbPostfix;
import com.persistentbit.sql.statement.annotations.DbPrefix;
import com.persistentbit.sql.statement.annotations.DbRename;

import java.util.function.Function;

/**
 * An implementation of an {@link ObjectWriter} that uses reflection to write an object to a database table row.<br>
 * Internally, {@link ImTools} is used to get all the properties from an Object.<br>
 * @see ObjectRowMapper
 */
public class DefaultObjectWriter implements ObjectWriter{
    private final ImTools im;


    private PMap<String,ObjectWriter> fieldWriters = PMap.empty();

    public DefaultObjectWriter(Class cls){
        this.im = ImTools.get(cls);

    }

    @Override
    public void write(String name,Object obj, ObjectWriter masterWriter, WritableRow result) {
        if(obj == null){
            return;
        }
        fieldWriters.forEach(t -> {
            Object fieldValue = im.get(obj,t._1);
            t._2.write(t._1,fieldValue,masterWriter,result);
        });
    }

    public DefaultObjectWriter addAllFields(){
        return addAllFieldsExcept();
    }

    public DefaultObjectWriter addAllFieldsExcept(String...fieldNames){
        PSet<String>  exclude= PStream.from(fieldNames).pset();
        PStream<ImTools.Getter> getters = im.getFieldGetters();
        fieldWriters = fieldWriters.plusAll(getters.filter(g -> exclude.contains(g.propertyName) == false && g.field.getAnnotation(DbIgnore.class)==null).map(g ->
            Tuple2.of(g.propertyName,new ObjectWriter(){
                @Override
                public void write(String fieldName, Object obj, ObjectWriter masterWriter, WritableRow result) {
                    masterWriter.write(g.propertyName,obj,masterWriter,result);
                }
            })

        ));
        getters.forEach(g -> {
            DbRename ren = g.field.getAnnotation(DbRename.class);
            if(ren != null){
                rename(g.propertyName,ren.value());
            }
            DbPrefix prefix = g.field.getAnnotation(DbPrefix.class);
            if(prefix != null){
                prefix(g.propertyName,prefix.value());
            }
            DbPostfix postfix = g.field.getAnnotation(DbPostfix.class);
            if(postfix!=null){
                postfix(g.propertyName,postfix.value());
            }
        });
        return this;
    }

    @Override
    public String toString() {
        return "DefaultObjectWriter(cls=" + im.getObjectClass().getSimpleName() + ", fieldWriters=" + fieldWriters.values().toString(",") + ")";
    }

    public DefaultObjectWriter rename(String fieldName, String propertyName){
        ObjectWriter orgWriter = getObjectWriter(fieldName);
        fieldWriters = fieldWriters.put(fieldName, new ObjectWriter() {
            @Override
            public void write(String name,Object obj, ObjectWriter masterWriter, WritableRow result) {
                orgWriter.write(propertyName,obj, masterWriter, new WritableRow() {
                    @Override
                    public WritableRow write(String name, Object value) {
                        return result.write(propertyName,value);
                    }
                });
            }
        });
        return this;
    }

    private ObjectWriter getObjectWriter(String fieldName) {
        ObjectWriter orgWriter = fieldWriters.get(fieldName);
        if(orgWriter == null){
            throw new IllegalArgumentException("Can't find field '" + fieldName + "'. Add the fields first with addAllFields() or addAllFieldsExcept()");
        }
        return orgWriter;
    }


    public DefaultObjectWriter mapToProperty(String fieldName,Function<Object,Object> fromFieldToProperty){
        ObjectWriter orgWriter = getObjectWriter(fieldName);
        fieldWriters = fieldWriters.put(fieldName, new ObjectWriter() {
            @Override
            public void write(String name,Object obj, ObjectWriter masterWriter, WritableRow result) {
                orgWriter.write(name,obj, masterWriter, new WritableRow() {
                    @Override
                    public WritableRow write(String name, Object value) {
                        result.write(name,fromFieldToProperty.apply(value));
                        return this;
                    }
                });
            }
        });
        return this;
    }

    public DefaultObjectWriter  prefix(String fieldName, String propertyPrefix){
        ObjectWriter orgWriter = getObjectWriter(fieldName);
        fieldWriters = fieldWriters.put(fieldName, new ObjectWriter() {
            @Override
            public void write(String name,Object obj, ObjectWriter masterWriter, WritableRow result) {
                orgWriter.write(name,obj, masterWriter, new WritableRow() {
                    @Override
                    public WritableRow write(String name, Object value) {
                        result.write(propertyPrefix+name,value);
                        return this;
                    }
                });
            }
        });
        return this;
    }

    public DefaultObjectWriter  postfix(String fieldName, String propertyPostfix){
        ObjectWriter orgWriter = getObjectWriter(fieldName);
        fieldWriters = fieldWriters.put(fieldName, new ObjectWriter() {
            @Override
            public void write(String name,Object obj, ObjectWriter masterWriter, WritableRow result) {
                orgWriter.write(name,obj, masterWriter, new WritableRow() {
                    @Override
                    public WritableRow write(String name, Object value) {
                        result.write(name + propertyPostfix,value);
                        return this;
                    }
                });
            }
        });
        return this;
    }


    public DefaultObjectWriter setFieldWriter(String name, ObjectWriter fieldWriter){
        fieldWriters = fieldWriters.put(name,fieldWriter);
        return this;
    }
}
