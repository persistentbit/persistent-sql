package com.persistentbit.sql.objectmappers;


import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.tuples.Tuple2;

/**
 * In in memory implementation of a readable and writable database row
 * @see ReadableRow
 * @see WritableRow
 */
public class InMemoryRow implements WritableRow,ReadableRow {
    static private final Tuple2<String,Object> nullTuple = Tuple2.of(null,null);
    private PMap<String,Tuple2<String,Object>>  all = PMap.empty();
    @Override
    public <T> T read(Class<T> cls, String name) {
        return  ReadableRow.checkAndConvert(cls,name,(T)all.getOrDefault(name.toLowerCase(),nullTuple)._2);

    }

    @Override
    public WritableRow write(String name, Object value) {
        all = all.put(name.toLowerCase(),Tuple2.of(name,value));
        return this;
    }

    @Override
    public String toString() {
        return "MemoryProperties[" + all.values().toString(",") + "]";
    }
}
