package com.persistentbit.sql.staticsql.expr;

/**
 * TODO: Add comment
 *
 * @author Peter Muys
 * @since 13/10/2016
 */
public class ExprStringLike implements ETypeBoolean{
    private     ETypeString left;
    private     ETypeString right;
    public ExprStringLike(ETypeString left, ETypeString right){
        this.left = left;
        this.right = right;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public ETypeString getLeft() {
        return left;
    }

    public ETypeString getRight() {
        return right;
    }
}
