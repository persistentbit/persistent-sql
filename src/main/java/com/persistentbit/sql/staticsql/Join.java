package com.persistentbit.sql.staticsql;

import com.persistentbit.core.utils.BaseValueClass;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 1/10/16.
 */
public class Join extends BaseValueClass{

    public enum Type {
        inner,left,right,full
    }
    private final Query query;
    private final ETypeObject table;
    private Expr joinExpr;
    private final Type type;


    public Join(Query query,Type type,ETypeObject table, Expr joinExpr) {
        this.query = query;
        this.type = type;
        this.table = table;
        this.joinExpr = joinExpr;
    }
    public Join(Query query,Type type,ETypeObject table) {
        this(query,type,table,null);
    }

    public Query on(Expr joinExpr){
        this.joinExpr = joinExpr;
        return query;
    }
    public Query query() {
        return query;
    }

    public ETypeObject getTable() {
        return table;
    }

    public Optional<Expr> getJoinExpr() {
        return Optional.ofNullable(joinExpr);
    }

    public Type getType() {
        return type;
    }
}
