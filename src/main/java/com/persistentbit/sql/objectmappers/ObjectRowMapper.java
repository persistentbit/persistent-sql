package com.persistentbit.sql.objectmappers;

import com.persistentbit.core.collections.PMap;

import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * This class maps the properties of Objects to and from a Database Row like structure.<br>
 * Use  the {@link #read(Class, ReadableRow)}  and {@link #write(Object, WritableRow)} methods to
 * map an object to and from a database row.<br>
 * Use {@link #registerReader(Class, ObjectReader)} and {@link #registerWriter(Class, ObjectWriter)} methods to
 * install custom mappers.<br>
 * Use {@link #createDefaultReader(Class)}, {@link #createDefaultWriter(Class)} or {@link #createDefault(Class)} to
 * create default reflection based readers that can be easily customized on a per property base.<br>
 */
public class ObjectRowMapper {
    static private final Logger log = Logger.getLogger(ObjectRowMapper.class.getName());

    /**
     * Default predicate to decide if a given class can be mapped to a column in a Row.
     */
    static public final Predicate<Class> defaultCanWriteInRowPredicate = cls ->  cls.equals(String.class) ||
            cls.equals(Boolean.class) ||
            cls.isPrimitive() ||
            Number.class.isAssignableFrom(cls) ||
            Temporal.class.isAssignableFrom(cls) ||
            Date.class.isAssignableFrom(cls);


    private PMap<Class,ObjectReader> readers = PMap.empty();
    private PMap<Class,ObjectWriter> writers = PMap.empty();


    private final Predicate<Class>  canWriteInRow;


    /**
     * Default mapper using the default predicate to decide if a given class can be mapped to a column in a Row.
     * @see #defaultCanWriteInRowPredicate
     */
    public ObjectRowMapper() {
        this(defaultCanWriteInRowPredicate);
    }

    /**
     * Create a new mapper.
     * @param canWriteInRow    Predicate to decide if a given class can be mapped to ca column in a row
     * @see #defaultCanWriteInRowPredicate
     */
    public ObjectRowMapper(Predicate<Class> canWriteInRow){
        this.canWriteInRow = canWriteInRow;
    }


    private final ObjectWriter  thisAsObjectWriter = new ObjectWriter() {
        @Override
        public void write(String name,Object obj, ObjectWriter masterWriter, WritableRow result) {
            ObjectRowMapper.this.write(name,obj,result);
        }
    };
    private final Function<Class,ObjectReader> thisAsReaderSupplier = cls ->  {
        ObjectReader reader = readers.get(Objects.requireNonNull(cls));
        if(reader == null){
            log.fine("No reader found for class " + cls  + " creating a default one");
            reader = createDefaultReader(cls).addAllFields();
        }
        return reader;
    };


    /**
     * Create a new Object of the provided Class, initialized using the provider row.<br>
     * If there is no registered ObjectReader, then a default one will be registered.
     *
     * @param name The name of the root object.
     * @param cls The class of the object to read. This determines the ObjectReader that is used to map the row
     * @param row The row with data
     * @return The mapped Object from the row or null if all properties in the row are null
     * @see DefaultObjectReader
     */
    public <T> T read(String name,Class<T> cls, ReadableRow row){

        return (T)thisAsReaderSupplier.apply(cls).read(name,thisAsReaderSupplier,row);
    }

    /**
     * Create a new Object of the provided Class, initialized using the provider row.<br>
     * If there is no registered ObjectReader, then a default one will be registered.
     *
     * @param cls The class of the object to read. This determines the ObjectReader that is used to map the row
     * @param row The row with data
     * @return The mapped Object from the row or null if all properties in the row are null
     * @see DefaultObjectReader
     */
    public <T> T read(Class<T> cls, ReadableRow row){
        return this.read("root",cls,row);
    }

    /**
     * Writes the provided Object to the WritableRow using a registered {@link ObjectWriter} for the objects class.<br>
     * If there is no registered ObjectWriter then a default one will be created that uses reflection.<br>
     * If the provided object is null then nothing will be written.<br>
     * @param name The name of the root object
     * @param obj The object to write in the row
     * @param result The row to write to
     * @see DefaultObjectWriter
     */
    public void write(String name,Object obj,WritableRow result){
        if(obj == null){
            return;
        }
        Class cls = obj.getClass();
        ObjectWriter writer = writers.get(cls);
        if(writer == null){
            log.fine("No writer found for class " + cls  + " creating a default one");
            writer = createDefaultWriter(cls).addAllFields();
        }
        writer.write(name,obj,thisAsObjectWriter,result);
    }

    /**
     * Writes the provided Object to the WritableRow using a registered {@link ObjectWriter} for the objects class.<br>
     * If there is no registered ObjectWriter then a default one will be created that uses reflection.<br>
     * If the provided object is null then nothing will be written.<br>
     * @param obj The object to write in the row
     * @param result The row to write to
     * @see DefaultObjectWriter
     */
    public void write(Object obj,WritableRow result) {
        write("root",obj,result);
    }

    /**
     * Register a custom {@link ObjectReader} for a given Class
     * @param cls The Class for the reader
     * @param reader The ObjectReader for the Class
     * @return me,myself and I
     */
    public ObjectRowMapper registerReader(Class cls, ObjectReader reader){
        this.readers = readers.put(cls,reader);
        return this;
    }

    /**
     * Register a custom {@link ObjectWriter} for a given Class
     * @param cls The Class for the writer
     * @param writer The ObjectWriter for the Class
     * @return me,myself and I
     */
    public ObjectRowMapper registerWriter(Class cls, ObjectWriter writer){
        this.writers = writers.put(cls,writer);
        return this;
    }

    /**
     * Create and register a {@link DefaultObjectWriter} for the provided class
     * @param cls The class to create a Writer for
     * @return The default writer
     */
    public DefaultObjectWriter createDefaultWriter(Class cls){
        DefaultObjectWriter writer = new DefaultObjectWriter(cls,canWriteInRow);
        registerWriter(cls,writer);
        return writer;
    }

    /**
     * Create and register a {@link DefaultObjectReader} for the provided class
     * @param cls The class to create a Reader for
     * @return The default reader
     */
    public DefaultObjectReader createDefaultReader(Class cls){
        DefaultObjectReader reader = new DefaultObjectReader(cls,canWriteInRow);
        registerReader(cls,reader);
        return reader;
    }

    /**
     * Create and register a convenient {@link DefaultObjectReaderWriter} instance that contains
     * both a created default reader and writer
     * @param cls The class for the object to  map
     * @return a combined reader and writer instance.
     * @see #createDefaultWriter(Class)
     * @see #createDefaultWriter(Class)
     */
    public DefaultObjectReaderWriter    createDefault(Class cls){
        return new DefaultObjectReaderWriter(createDefaultReader(cls),createDefaultWriter(cls));
    }




}
