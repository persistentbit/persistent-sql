package com.persistentbit.sql.staticsql;


import com.persistentbit.core.tuples.Tuple2;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprJoin<L,R> implements ETypeObject<Tuple2<L,R>> {
    private final ETypeObject<L> left;
    private final ETypeObject<R> right;
    private final ETypeBoolean  on;

    public ExprJoin(ETypeObject<L> left, ETypeObject<R> right,ETypeBoolean on) {
        this.left = left;
        this.right = right;
        this.on = on;
    }

    @Override
    public String toString() {
        return left.toString() + " join " + right + " on " + on;
    }
}
