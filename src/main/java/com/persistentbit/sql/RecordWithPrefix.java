package com.persistentbit.sql;

/**
 * @author Peter Muys
 * @since 4/07/2016
 */
public class RecordWithPrefix implements Record{
    private final String prefix;
    private final Record parent;

    public RecordWithPrefix(String prefix, Record parent){
        this.prefix = prefix;
        this.parent = parent;
    }
    @Override
    public boolean hasName(String name) {
        return parent.hasName(prefix+name);
    }

    @Override
    public Object getObject(String name) {
        return parent.getObject(prefix+name);
    }
}
