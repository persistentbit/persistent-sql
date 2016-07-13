package com.persistentbit.sql;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class RecordImpl implements Record{
    private final PMap<String,Integer> fieldNameIndexes;
    private final Object[]  values;
    public RecordImpl(PMap<String,Integer> fieldNameIndexes, Object[] values){
        this.fieldNameIndexes = fieldNameIndexes;
        this.values = values;
    }

    @Override
    public PStream<String> getNames() {
        return fieldNameIndexes.keys();
    }

    public boolean hasName(String name){
        return fieldNameIndexes.containsKey(name.toLowerCase());
    }
    public Object getObject(String naam){
        Integer index = fieldNameIndexes.get(naam.toLowerCase());
        if(index == null || index == -1){
            throw new RuntimeException("Can't find record field '" + naam + "' in "+ fieldNameIndexes);
        }
        return values[index];
    }


    public String toString() {
        return "Record[" +getAll().mapString().join((a,b)->",").orElse("") + "]";

    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) { return true; }
        if(obj instanceof Record == false) { return false; }
        Record other = (Record)obj;
        return getAll().equals(other.getAll());
    }

    @Override
    public int hashCode() {
        return getAll().hashCode();
    }
}