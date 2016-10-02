package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeNumber;
import com.persistentbit.sql.staticsql.expr.ETypeString;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprNumberToString implements ETypeString {
    private ETypeNumber number;

    public ExprNumberToString(ETypeNumber number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "((String)" + number + ")";
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public ETypeNumber getNumber() {
        return number;
    }
}
