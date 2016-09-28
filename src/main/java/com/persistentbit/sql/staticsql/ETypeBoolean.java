package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeBoolean extends Expr<Boolean>{
    default ETypeBoolean and(ETypeBoolean right){
        return new ExprAndOr(this,right, ExprAndOr.LogicType.and);
    }
    default ETypeBoolean or(ETypeBoolean right){
        return new ExprAndOr(this,right, ExprAndOr.LogicType.or);
    }

}
