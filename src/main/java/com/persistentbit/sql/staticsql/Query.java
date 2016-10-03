package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 1/10/16.
 */
public class Query {
    private final DbSql dbSql;
    private final ETypeObject from;
    private PList<Join> joins;
    private ETypeBoolean where;

    public Query(DbSql sql,ETypeObject from, PList<Join> joins) {
        this.dbSql = sql;
        this.from = from;
        this.joins = joins;
    }

    static public Query from(DbSql sql,ETypeObject table){
        return new Query(sql,table,PList.empty());
    }



    private Join add(Join j){
        joins = joins.plus(j);
        return j;
    }

    public Join leftJoin(ETypeObject table){
        return add(new Join(this,Join.Type.left,table));
    }

    public Join rightJoin(ETypeObject table){
        return add(new Join(this,Join.Type.right,table));
    }
    public Join innerJoin(ETypeObject table){
        return add(new Join(this,Join.Type.inner,table));
    }
    public Join fullJoin(ETypeObject table){
        return add(new Join(this,Join.Type.full,table));
    }
    public Query where(ETypeBoolean whereExpr){
        this.where = whereExpr;
        return this;
    }


    public ETypeObject getFrom() {
        return from;
    }

    public PList<Join> getJoins() {
        return joins;
    }

    public Optional<ETypeBoolean> getWhere() {
        return Optional.ofNullable(where);
    }

    public <T> Selection<T> selection(Expr<T> selection){
        return new Selection<T>(this,selection);
    }

    DbSql getDbSql() {
        return dbSql;
    }
}
