package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeString;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprStringAdd  implements ETypeString {
    private ETypeString left;
    private ETypeString right;

    public ExprStringAdd(ETypeString left, ETypeString right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left + "+" + right;
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public ETypeString getLeft() {
        return left;
    }

    public ETypeString getRight() {
        return right;
    }
}
