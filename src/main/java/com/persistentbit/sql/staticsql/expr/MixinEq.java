package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.ExprCompare;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface MixinEq<T extends Expr> {

    default ETypeBoolean    eq(T right){
        return new ExprCompare((T)this,right, ExprCompare.CompType.eq);
    }
    default ETypeBoolean notEq(T right){
        return new ExprCompare((T)this,right, ExprCompare.CompType.neq);
    }
}
