package com.persistentbit.sql.staticsql;


import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.NotYet;
import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprJoin<L,R> implements ETypeObject<Tuple2<L,R>> {
    private final ETypeObject<L> left;
    private final ETypeObject<R> right;
    private final ETypeBoolean on;

    public ExprJoin(ETypeObject<L> left, ETypeObject<R> right, ETypeBoolean on) {
        this.left = left;
        this.right = right;
        this.on = on;
    }

    @Override
    public String toString() {
        return left.toString() + " join " + right + " on " + on;
    }

    @Override
    public PList<Expr> _all() {
        throw new NotYet();
    }
}
