package com.persistentbit.sql.references;

import com.persistentbit.core.properties.FieldNames;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class StringRef<R> extends RefId<R,String> {
    @FieldNames(names={"id"})
    public StringRef(String id) {
        super(id);
    }
    @Override
    public String toString() {
        return "StringRef("+ getId().orElse("") + ")";
    }

}
