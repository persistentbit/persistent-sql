package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeNumber;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprNumberBinOp<N extends Number> implements ETypeNumber<N> {
    private ETypeNumber<N> left;
    private ETypeNumber<N> right;
    private String binOp;

    public ExprNumberBinOp(ETypeNumber<N> left, ETypeNumber<N> right,String binOp) {
        this.left = left;
        this.right = right;
        this.binOp = binOp;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + binOp + " " + right + ")";
    }
}
