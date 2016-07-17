package com.persistentbit.sql.statement;

import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.dbdef.TableDef;
import com.persistentbit.sql.objectmappers.ReadableRow;

/**
 * Created by petermuys on 16/07/16.
 */
public interface EJoinable<T> {
    String getName();
    String getSelectPart();
    String getTableName();
    String getOwnJoins();
    T mapRow(Record row);

    SQLRunner   getRunner();
    EStatementPreparer  getStatementPreparer();

}
