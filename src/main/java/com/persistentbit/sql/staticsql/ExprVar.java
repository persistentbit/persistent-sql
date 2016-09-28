package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprVar<T> implements Expr<T>{
    private String varName;

    public ExprVar(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return varName;
    }
}
