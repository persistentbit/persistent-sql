package com.persistentbit.sql.statement;

import com.persistentbit.sql.dbdef.TableDef;

/**
 * Created by petermuys on 16/07/16.
 */
public interface Joinable {
    Class getMappedClass();
    TableDef getTableDef();
}
