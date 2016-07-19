package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.connect.SQLRunner;

/**
 * Created by petermuys on 16/07/16.
 */
public interface EJoinable {
    String getName();
    String getSelectPart();
    String getTableName();
    String getOwnJoins();
    PList<Object> mapRow(Record row);

    SQLRunner   getRunner();
    EStatementPreparer  getStatementPreparer();



}
