package com.persistentbit.sql.staticsql;

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
}