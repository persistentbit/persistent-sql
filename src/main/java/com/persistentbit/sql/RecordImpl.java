package com.persistentbit.sql;

import com.persistentbit.core.collections.PMap;

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
        String res = "";
        for(int t=0; t<values.length;t++){
            if(t != 0) { res += ", ";}
            res += values[t];
        }
        return "Record[" + res + "]";
    }


}