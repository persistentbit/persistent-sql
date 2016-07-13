package com.persistentbit.sql;

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
public interface Record {
    boolean hasName(String name);

    Object getObject(String naam);

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
