package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.function.Function2;
import com.persistentbit.core.function.Function3;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.tuples.Tuple3;

/**
 * Created by petermuys on 2/10/16.
 */
public class ETuple3<T1,T2,T3> implements Expr<Tuple3<T1,T2,T3>>{
    private Expr<T1> v1;
    private Expr<T2> v2;
    private Expr<T3> v3;

    public ETuple3(Expr<T1> v1, Expr<T2> v2, Expr<T3> v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public <R> Expr<R> map(Function3<T1,T2,T3,R> mapper){
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

    public Expr<T3> getV3() {
        return v3;
    }
}
