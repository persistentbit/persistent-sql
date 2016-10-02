package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeString;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprConstString implements ETypeString {
    private final String value;

    public ExprConstString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public String getValue() {
        return value;
    }
}
