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


    default EJoinStats  fullOuterJoin(EJoinable other,String joinSql){
        return join("FULL OUTER JOIN",other,joinSql);
    }
    default EJoinStats  leftOuterJoin(EJoinable other,String joinSql){
        return join("LEFT OUTER JOIN",other,joinSql);
    }
    default EJoinStats  rightOuterJoin(EJoinable other,String joinSql){
        return join("RIGHT OUTER JOIN",other,joinSql);
    }
    default EJoinStats  innerJoin(EJoinable other,String joinSql){
        return join("INNER JOIN",other,joinSql);
    }
    default EJoinStats join(String joinType,EJoinable other,String joinSql){
        return new EJoinStats(this,other,joinType,joinSql);
    }

}
