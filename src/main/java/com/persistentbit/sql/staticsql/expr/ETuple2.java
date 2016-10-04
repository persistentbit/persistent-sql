package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.function.Function2;
import com.persistentbit.core.tuples.Tuple2;

/**
 * Created by petermuys on 2/10/16.
 */
public class ETuple2<T1,T2> implements Expr<Tuple2<T1,T2>>{
    private Expr<T1> v1;
    private Expr<T2> v2;

    public ETuple2(Expr<T1> v1, Expr<T2> v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public <T3> ETuple3<T1,T2,T3> add(Expr<T3> expr){
        return new ETuple3<>(v1,v2,expr);
    }

    public <R> Expr<R> map(Function2<T1,T2,R> mapper){
        return new EMapper<>(this,(t -> t.map(mapper)));
    }

    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public Expr<T1> getV1() {
        return v1;
    }

    public Expr<T2> getV2() {
        return v2;
    }
}