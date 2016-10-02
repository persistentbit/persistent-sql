package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeBoolean;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprAndOr implements ETypeBoolean{
    public enum LogicType{
        and, or
    }
    private final ETypeBoolean left;
    private final ETypeBoolean right;
    private final LogicType logicType;

    public ExprAndOr(ETypeBoolean left, ETypeBoolean right, LogicType logicType) {
        this.left = left;
        this.right = right;
        this.logicType = logicType;
    }

    @Override
    public String toString() {
        return left.toString() + " " + logicType + " " + right.toString();
    }

    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public ETypeBoolean getLeft() {
        return left;
    }

    public ETypeBoolean getRight() {
        return right;
    }

    public LogicType getLogicType() {
        return logicType;
    }
}
