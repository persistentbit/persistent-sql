package com.persistentbit.sql;

import com.persistentbit.core.collections.PStream;

/**
 * @author Peter Muys
 * @since 4/07/2016
 */
public class RecordSubSet implements Record{
    private final String prefix;
    private final Record parent;

    public RecordSubSet(String prefix, Record parent){
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

    @Override
    public PStream<String> getNames() {
        return parent.getNames().filter(n->n.startsWith(prefix)).map(n->n.substring(prefix.length()));
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
    public String toString() {
        return "Recordsubset(" + prefix + ")[" +getAll().mapString().toString(",") + "]";

    }

}
