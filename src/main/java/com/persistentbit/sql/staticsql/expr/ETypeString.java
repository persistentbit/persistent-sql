package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeString  extends Expr<String>,MixinEq<ETypeString>,MixinComparable<ETypeString> {

    default ETypeBoolean    eq(String other){
        return eq(Expr.val(other));
    }
    default ETypeBoolean notEq(String other){
        return notEq(Expr.val(other));
    }


    default ETypeString add(ETypeString expr){
        return new ExprStringAdd(this,expr);
    }

    default ETypeString add(String value){
        return add(Expr.val(value));
    }
}
