package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprJoin;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeObject<T> extends Expr<T>{
    default ETypeObject join(ETypeObject obj, ETypeBoolean on){
        return new ExprJoin(this,obj,on);
    }

     PList<Expr> _all();
}
