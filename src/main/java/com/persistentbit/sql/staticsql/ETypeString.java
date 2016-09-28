package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeString  extends Expr<String>,MixinEq<ETypeString>,MixinComparable<ETypeString> {

    default ETypeString add(ETypeString expr){
        return new ExprStringAdd(this,expr);
    }

    default ETypeString add(String value){
        return add(Expr.val(value));
    }
}
