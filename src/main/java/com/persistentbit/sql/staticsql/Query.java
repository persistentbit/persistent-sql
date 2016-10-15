package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

/**
 * Created by petermuys on 1/10/16.
 */
public class Query {
    private final DbSql dbSql;
    private final ETypeObject from;
    private PList<Join> joins;
    private ETypeBoolean where;
    boolean distinct = false;
    PList<OrderBy>  orderBy = PList.empty();

    public Query(DbSql sql,ETypeObject from, PList<Join> joins) {
        this.dbSql = sql;
        this.from = from;
        this.joins = joins;
    }

    static public Query from(DbSql sql,ETypeObject table){
        return new Query(sql,table,PList.empty());
    }


    public Query distinct() {
        distinct = true;
        return this;
    }


    public Query orderByDesc(Expr<?> expr){
        return orderBy(new OrderBy(expr, OrderBy.Direction.desc));
    }
    public Query orderByAsc(Expr<?> expr){
        return orderBy(new OrderBy(expr, OrderBy.Direction.asc));
    }
    public Query orderBy(OrderBy orderBy){
        this.orderBy = this.orderBy.plus(orderBy);
        return this;
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

    public <T> Selection1<T> selection(Expr<T> selection){
        return new Selection1<T>(this,selection);
    }

    public <T1,T2> Selection2<T1,T2> selection(
            Expr<T1> col1, Expr<T2> col2
    ){
        return new Selection2<>(this,col1,col2);
    }

    public <T1,T2,T3> Selection3<T1,T2,T3> selection(
            Expr<T1> col1, Expr<T2> col2, Expr<T3> col3
    ){
        return new Selection3<>(this,col1,col2,col3);
    }

    public <T1,T2,T3,T4> Selection4<T1,T2,T3,T4> selection(
            Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4
    ){
        return new Selection4<>(this,col1,col2,col3,col4);
    }

    public <T1,T2,T3,T4,T5> Selection5<T1,T2,T3,T4,T5> selection(
            Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4, Expr<T5> col5
    ){
        return new Selection5<>(this,col1,col2,col3,col4,col5);
    }

    public <T1,T2,T3,T4,T5,T6> Selection6<T1,T2,T3,T4,T5,T6> selection(
            Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4, Expr<T5> col5, Expr<T6> col6
    ){
        return new Selection6<>(this,col1,col2,col3,col4,col5,col6);
    }

    public <T1,T2,T3,T4,T5,T6,T7> Selection7<T1,T2,T3,T4,T5,T6,T7> selection(
            Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4, Expr<T5> col5, Expr<T6> col6, Expr<T7> col7
            ){
        return new Selection7<>(this,col1,col2,col3,col4,col5,col6,col7);
    }

    public DbSql getDbSql() {
        return dbSql;
    }
}
