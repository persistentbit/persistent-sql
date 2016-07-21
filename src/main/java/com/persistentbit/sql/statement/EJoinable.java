package com.persistentbit.sql.statement;

import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.dbdef.TableDef;

/**
 * Created by petermuys on 16/07/16.
 */
public interface EJoinable<JT> {
    String getName();
    String getSelectPart();
    String getTableName();
    String getOwnJoins();
    JT mapRow(Record row);

    SQLRunner   getRunner();
    EStatementPreparer  getStatementPreparer();
    DbType  getDbType();

    TableDef    getTableDef();



}
