package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.function.Function4;
import com.persistentbit.core.tuples.Tuple4;

/**
 * Created by petermuys on 3/10/16.
 */
public class ETuple4<T1,T2,T3,T4> implements Expr<Tuple4<T1,T2,T3,T4>>{
    private Expr<T1> v1;
    private Expr<T2> v2;
    private Expr<T3> v3;
    private Expr<T4> v4;


    public ETuple4(Expr<T1> v1, Expr<T2> v2, Expr<T3> v3, Expr<T4> v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    public <R> Expr<R> map(Function4<T1,T2,T3,T4,R> mapper){
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
    public Expr<T4> getV4() {
        return v4;
    }


}
