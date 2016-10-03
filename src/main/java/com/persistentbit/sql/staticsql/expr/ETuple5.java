package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.function.Function5;
import com.persistentbit.core.function.Function6;
import com.persistentbit.core.tuples.Tuple5;
import com.persistentbit.core.tuples.Tuple6;

/**
 * Created by petermuys on 3/10/16.
 */
public class ETuple5<T1,T2,T3,T4,T5> implements Expr<Tuple5<T1,T2,T3,T4,T5>>{
    private Expr<T1> v1;
    private Expr<T2> v2;
    private Expr<T3> v3;
    private Expr<T4> v4;
    private Expr<T5> v5;


    public ETuple5(Expr<T1> v1, Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
    }

    public <R> Expr<R> map(Function5<T1,T2,T3,T4,T5,R> mapper){
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
    public Expr<T5> getV5() {
        return v5;
    }


}
