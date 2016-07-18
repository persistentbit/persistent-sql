package com.persistentbit.sql.references;

import com.persistentbit.core.properties.FieldNames;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class LongRef<R> extends RefId<R,Long>{
    @FieldNames(names={"id"})
    public LongRef(Long id) {
        super(id);
    }

    @Override
    public String toString() {
        return "LongRef("+ getId().orElse(0L) + ")";
    }
}
