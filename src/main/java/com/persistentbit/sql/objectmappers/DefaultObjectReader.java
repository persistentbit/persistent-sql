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

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * An implementation of an {@link ObjectReader} that uses reflection to read an object from a database table row.<br>
 * Internally, {@link ImTools} is used to get all the properties from an Object.<br>
 *
 * @see ObjectRowMapper
 */
public class DefaultObjectReader implements ObjectReader {
    private final ImTools im;
    private PMap<String, ObjectReader> fieldReaders = PMap.empty();


    public DefaultObjectReader(Class cls) {
        im = ImTools.get(cls);
    }


    @Override
    public String toString() {
        return "DefaultObjectReader[" + im.getObjectClass().getSimpleName() + ", " + fieldReaders + "]";
    }

    @Override
    public Object read(Type typeToRead, String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {

        PMap<String, Object> map = fieldReaders.mapKeyValues(t ->

                Tuple2.of(t._1, t._2.read(typeToRead, t._1, readerSupplier, properties))
        );
        if (map.values().find(v -> v != null).isPresent() == false) {
            //Not 1 property is set, assuming this is a null value
            return null;
        }
        return im.createNew(map);
    }

    private Object mapProperty(Class type, Object value) {
        if (value == null) {
            return null;
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return ((Number) value).intValue();

        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            return ((Number) value).longValue();

        }
        return value;
    }


    public DefaultObjectReader addAllFields() {
        return addAllFieldsExcept();
    }

    public DefaultObjectReader addAllFieldsExcept(String... fieldNames) {
        PSet<String> exclude = PStream.from(fieldNames).pset();
        PStream<ImTools.Getter> getters = im.getFieldGetters();
        fieldReaders = fieldReaders.plusAll(getters.filter(g -> exclude.contains(g.propertyName) == false && g.field.getAnnotation(DbIgnore.class)==null).map(g ->
                Tuple2.of(g.propertyName, new ObjectReader() {
                    @Override
                    public Object read(Type type,String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                        return readerSupplier.apply(g.field.getType()).read(g.getter.getPropertyType(),g.propertyName, readerSupplier, properties);
                    }

                    @Override
                    public String toString() {
                        return "FieldReader[" + im.getObjectClass().getSimpleName() + ",  " + g.field.getType().getSimpleName() + "." + g.propertyName + "]";
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
            if(postfix != null){
                postfix(g.propertyName,postfix.value());
            }
        });
        return this;
    }

    public DefaultObjectReader rename(String fieldName, String propertyName) {
        ObjectReader orgReader = getObjectReader(fieldName);
        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public String toString() {
                return "RenamedReader(fieldName=" + fieldName + ", propName=" + propertyName + ", reader=" + orgReader + ")";
            }

            @Override
            public Object read(Type type,String name, Function<Class, ObjectReader> masterReader, ReadableRow properties) {
                return orgReader.read(type,propertyName, masterReader, new ReadableRow() {
                    @Override
                    public <T> T read(Class<T> cls, String name) {
                        if(name.equals(fieldName)){
                            name = propertyName;
                        }
                        return ReadableRow.check(cls,name,properties.read(cls,name));

                    }
                });
            }
        });
        return this;
    }

    public DefaultObjectReader mapToField(String fieldName, Function<Object, Object> fromPropertyToField) {
        ObjectReader orgReader = getObjectReader(fieldName);
        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public Object read(Type type,String name, Function<Class, ObjectReader> masterReader, ReadableRow properties) {
                return fromPropertyToField.apply(orgReader.read(type,fieldName, masterReader, properties));
            }
        });
        return this;
    }

    public DefaultObjectReader prefix(String fieldName, String propertyPrefix) {
        ObjectReader orgReader = getObjectReader(fieldName);

        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public Object read(Type type,String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                return orgReader.read(type,name, readerSupplier, new ReadableRow() {
                    @Override
                    public <T> T read(Class<T> cls, String name) {
                        return ReadableRow.check(cls,name,(T)properties.read(cls, propertyPrefix + name));

                    }
                });
            }
        });
        return this;
    }
    public DefaultObjectReader postfix(String fieldName, String propertyPostfix) {
        ObjectReader orgReader = getObjectReader(fieldName);

        fieldReaders = fieldReaders.put(fieldName, new ObjectReader() {
            @Override
            public Object read(Type type,String name, Function<Class, ObjectReader> readerSupplier, ReadableRow properties) {
                return orgReader.read(type,name, readerSupplier, new ReadableRow() {
                    @Override
                    public <T> T read(Class<T> cls, String name) {
                        return ReadableRow.check(cls,name,(T)properties.read(cls, name + propertyPostfix));

                    }
                });
            }
        });
        return this;
    }

    private ObjectReader getObjectReader(String fieldName) {
        ObjectReader orgReader = fieldReaders.get(fieldName);
        if (orgReader == null) {
            throw new IllegalArgumentException("Can't find field '" + fieldName + "'. Add the fields first with addAllFields() or addAllFieldsExcept()");
        }
        return orgReader;
    }


    public DefaultObjectReader setFieldReader(String name, ObjectReader fieldReader) {
        fieldReaders = fieldReaders.put(name, fieldReader);
        return this;
    }
}
