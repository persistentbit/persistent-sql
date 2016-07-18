package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.objectmappers.ReadableRow;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Peter Muys
 * @since 4/07/2016
 */
public interface Record extends ReadableRow{
    boolean hasName(String name);

    PStream<String> getNames();

    Object getObject(String naam);


    @Override
    default <T> T read(Class<T> cls, String name) {
        return (T)getObject(name);
    }

    default PMap<String,Object> getAll(){
        return getNames().with(PMap.<String,Object>empty(),(m,n)-> m = m.put(n,getObject(n)));
    }

    default Record getSubRecord(String name){
        return new RecordSubSet(name+"_",this);
    }

    /*
    default Record lazyMapSubRecord(String subName, Function<Record,?> mapped){
        final String lowName = subName.toLowerCase();
        return new Record(){
            @Override
            public boolean hasName(String name) {
                if(name.equals(lowName)){
                    return true;
                }
                if(name.startsWith(lowName)){
                    return false;
                }
                return Record.this.hasName(name);
            }

            @Override
            public PStream<String> getNames() {
                return Record.this.getNames().filter(n -> n.startsWith(lowName)== false).plus(lowName);
            }

            @Override
            public Object getObject(String naam) {
                if(lowName.equals(naam.toLowerCase())){
                    return mapped.apply(new RecordSubSet(subName+"_",Record.this));
                }
                return Record.this.getObject(naam);
            }
        };
    }*/

    default Number getNumber(String naam) {
        return (Number) getObject(naam);
    }

    default Integer getInt(String naam) {
        Number n = getNumber(naam);
        return n == null ? null : n.intValue();
    }

    default Long getLong(String naam) {
        Number n = getNumber(naam);
        return n == null ? null : n.longValue();
    }

    default Float getFloat(String naam) {
        Number n = getNumber(naam);
        return n == null ? null : n.floatValue();
    }

    default Double getDouble(String naam) {
        Number n = getNumber(naam);
        return n == null ? null : n.doubleValue();
    }

    default Date getDate(String name) {
        Date d = ((Date) getObject(name));
        if (d == null) {
            return null;
        }
        return new Date(d.getTime());
    }

    default LocalDate getLocalDate(String name) {
        Date d = getDate(name);
        if (d == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(d.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .toLocalDate();
    }

    default LocalDateTime getLocalDateTime(String name) {
        Timestamp timestamp = (Timestamp) getObject(name);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    default Boolean getBoolean(String naam) {
        return (Boolean) getObject(naam);
    }

    default String getString(String naam) {
        return (String) getObject(naam);
    }

    default String getString(String naam, String defaultValue){
        String res = getString(naam);
        return res == null ? defaultValue : res;
    }

    default <E extends Enum> E getEnum(Class<E> cls, String name) {
        String value = getString(name);
        if (value == null) {
            return null;
        }
        try {
            Field f = cls.getDeclaredField(value);
            return (E) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Can't find value '" + value + "' in enum " + cls.getSimpleName(), e);
        }
    }

}
