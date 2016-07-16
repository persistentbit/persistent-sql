package com.persistentbit.sql.objectmappers;

import java.util.function.Function;

/**
 * A Wrapper around a {@link DefaultObjectReader} and a {@link DefaultObjectWriter} for the same class.<br>
 * This class makes it easy to customise the {@link DefaultObjectReader} and {@link DefaultObjectWriter} at the same time.<br>
 */
public class DefaultObjectReaderWriter {
    private final DefaultObjectReader reader;
    private final DefaultObjectWriter writer;

    /**
     * Init with the reader an writer for a Class.
     * @param reader The {@link DefaultObjectReader} for the class
     * @param writer The {@link DefaultObjectWriter} for the class.
     */
    public DefaultObjectReaderWriter(DefaultObjectReader reader, DefaultObjectWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Prefix the row column names with the given prefix on reading and writing<br>
     * Warning:There should already be a field mapper registered. See {@link #addAllFields()}<br>
     * @param fieldName The Object property name
     * @param prefix The prefix string
     * @return me,myself and I
     *
     */
    public DefaultObjectReaderWriter prefix(String fieldName, String prefix){
        reader.prefix(fieldName,prefix);
        writer.prefix(fieldName,prefix);
        return this;
    }

    /**
     * Renames the row column for a field.<br>
     * Warning: The field should be writable to the row (like Strings, numbers,booleans...)<br>
     * Warning:There should already be a field mapper registered. See {@link #addAllFields()}<br>
     * @param fieldName The FieldName
     * @param propertyName The row column name
     * @return me,myself and I
     */
    public DefaultObjectReaderWriter rename(String fieldName, String propertyName){
        reader.rename(fieldName,propertyName);
        writer.rename(fieldName,propertyName);
        return this;
    }

    /**
     * Create a field {@link ObjectReader} and {@link ObjectWriter} for each field in this mapped class
     * that is not in the provided expetion list<br>
     * @param fieldNames The names of the fields to exclude
     * @return me,myself and I
     */
    public DefaultObjectReaderWriter addAllFieldsExcept(String...fieldNames){
        reader.addAllFieldsExcept(fieldNames);
        writer.addAllFieldsExcept(fieldNames);
        return this;
    }

    /**
     * Create a field {@link ObjectReader} and {@link ObjectWriter} for each field in this mapped class
     * @return me,myself and I
     */
    public DefaultObjectReaderWriter addAllFields() {
        return addAllFieldsExcept();
    }

    /**
     * Maps the values to and from a row.
     * @param fieldName The fieldname to map for
     * @param toProperty mapper to convert the object field value to a property field value
     * @param toField mapper to convert the row column value to an object field value
     * @param <F> The type of the field value
     * @param <P> The type of the row column value
     * @return me,myself and I
     */
    public <F,P> DefaultObjectReaderWriter  mapField(String fieldName,Function<F,P> toProperty, Function<P,F> toField){
        reader.mapToField(fieldName, (Function<Object,Object>)toField);
        writer.mapToProperty(fieldName, (Function<Object,Object>)toProperty);
        return this;
    }

    /**
     * Set a custom Field reader
     * @param fieldName The name of the field
     * @param fieldReader The custom reader
     * @return me,myself and I
     */
    public DefaultObjectReaderWriter    setFieldReader(String fieldName, ObjectReader fieldReader){
        reader.setFieldReader(fieldName,fieldReader);
        return this;
    }

    /**
     * Set a custom Field writer
     * @param fieldName The name of the field
     * @param fieldWriter The custom writer
     * @return me,myself and I
     */
    public DefaultObjectReaderWriter    setFieldWriter(String fieldName, ObjectWriter fieldWriter){
        writer.setFieldWriter(fieldName,fieldWriter);
        return this;
    }

    public DefaultObjectReaderWriter readFieldAsInt(String fieldName){
        reader.mapToField(fieldName, f -> {
            if(f instanceof Integer == false){
                Number n = (Number)f;
                return n.intValue();
            }
            return f;
        });
        return this;
    }
}
