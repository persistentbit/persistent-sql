package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprCompare<T extends Expr> implements ETypeBoolean {

    public enum CompType{
        eq("=="),lt("<"),gt(">"),ltEq("<="),gtEq(">="),neq("!=");
        private String token;
        CompType(String token){
            this.token = token;
        }

        @Override
        public String toString() {
            return token;
        }
    }
    private final T   left;
    private final T   right;
    private final CompType  compType;

    public ExprCompare(T left, T right, CompType compType) {
        this.left = left;
        this.right = right;
        this.compType = compType;
    }

    @Override
    public String toString() {
        return left.toString() + compType + right.toString();
    }
}
