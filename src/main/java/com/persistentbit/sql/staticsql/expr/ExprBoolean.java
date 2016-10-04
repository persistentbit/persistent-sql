package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeBoolean;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprBoolean implements ETypeBoolean {
    private final Boolean value;

    public ExprBoolean(Boolean value) {
        this.value = value;
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public Boolean getValue() {
        return value;
    }
}