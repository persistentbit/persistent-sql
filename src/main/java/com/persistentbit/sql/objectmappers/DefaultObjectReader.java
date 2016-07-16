package com.persistentbit.sql.objectmappers;

import com.persistentbit.core.Tuple2;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.utils.ImTools;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An implementation of an {@link ObjectReader} that uses reflection to read an object from a database table row.<br>
 * Internally, {@link ImTools} is used to get all the properties from an Object.<br>
 * @see ObjectRowMapper
 */
public class DefaultObjectReader implements ObjectReader{
    private final ImTools im;
    private PMap<String,ObjectReader> fieldReaders = PMap.empty();
    private final Predicate<Class> canWriteToRow;

    public DefaultObjectReader(Class cls,Predicate<Class> canWriteToRow){
        im = ImTools.get(cls);
        this.canWriteToRow = canWriteToRow;
    }

    @Override
    public Object read(Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {

        PMap<String,Object> map = fieldReaders.mapKeyValues(t -> Tuple2.of(t._1,t._2.read(readerSupplier,properties)));
        if(map.values().find(v -> v!= null).isPresent() == false){
            //Not 1 property is set, assuming this is a null value
            return null;
        }
        return im.createNew(map);
    }



    public DefaultObjectReader addAllFields(){
        return addAllFieldsExcept();
    }

    public DefaultObjectReader addAllFieldsExcept(String...fieldNames){
        PSet<String> exclude= PStream.from(fieldNames).pset();
        PStream<ImTools.Getter> getters = im.getFieldGetters();
        fieldReaders = fieldReaders.plusAll(getters.filter(g -> exclude.contains(g.propertyName) == false).map(g -> {
            ObjectReader fw;
            Class fieldClass = g.field.getType();
            if(canWriteToRow.test(fieldClass)){
                fw = new ObjectReader() {
                    @Override
                    public Object read(Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                        return properties.read(g.propertyName);
                    }

                    @Override
                    public String toString() {
                        return "ValueReader(cls=" + fieldClass.getSimpleName() + ", name=" + g.propertyName + ")";
                    }
                };
            } else {
                fw = new ObjectReader() {
                    @Override
                    public Object read(Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                        return readerSupplier.apply(fieldClass).read(readerSupplier,properties);
                    }

                    @Override
                    public String toString() {
                        return "ObjectReader(cls=" + fieldClass + ")";
                    }
                };
            }
            return new Tuple2<>(g.propertyName,fw);
        }));
        return this;
    }

    public DefaultObjectReader rename(String fieldName, String propertyName){
        ObjectReader orgReader = getObjectReader(fieldName);
        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public Object read(Function<Class, ObjectReader> masterReader, ReadableRow properties) {
                return orgReader.read(masterReader, name -> {
                    if(name.equalsIgnoreCase(propertyName)){
                        name = propertyName;
                    }
                    return properties.read(name);
                });
            }
        });
        return this;
    }

    public DefaultObjectReader mapToField(String fieldName, Function<Object,Object> fromPropertyToField){
        ObjectReader orgReader = getObjectReader(fieldName);
        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public Object read(Function<Class, ObjectReader> masterReader, ReadableRow properties) {
                return fromPropertyToField.apply(orgReader.read(masterReader,properties));
            }
        });
        return this;
    }

    public DefaultObjectReader  prefix(String fieldName, String propertyPrefix){
        ObjectReader orgReader = getObjectReader(fieldName);
        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public Object read(Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                return orgReader.read(readerSupplier, name -> {
                    return properties.read(propertyPrefix+name);
                });
            }
        });
        return this;
    }

    private ObjectReader getObjectReader(String fieldName) {
        ObjectReader orgReader = fieldReaders.get(fieldName);
        if(orgReader == null){
            throw new IllegalArgumentException("Can't find field '" + fieldName + "'. Add the fields first with addAllFields() or addAllFieldsExcept()");
        }
        return orgReader;
    }


    public DefaultObjectReader setFieldReader(String name, ObjectReader fieldReader){
        fieldReaders = fieldReaders.put(name,fieldReader);
        return this;
    }
}
