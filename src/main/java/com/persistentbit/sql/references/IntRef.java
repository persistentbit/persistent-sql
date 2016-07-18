package com.persistentbit.sql.references;

import com.persistentbit.core.properties.FieldNames;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class IntRef<R> extends RefId<R,Integer>{
    @FieldNames(names={"id"})
    public IntRef(Integer id) {
        super(id);
    }
    @Override
    public String toString() {
        return "IntRef("+ getId().orElse(0) + ")";
    }

}
