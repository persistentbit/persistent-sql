package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface Expr<S>{

    static <N extends Number> ExprConstNumber<N> val(N number){
        return new ExprConstNumber<N>(number);
    }
    static ETypeString  val(String value){
        return new ExprConstString(value);
    }

    static ETypeBoolean val(Boolean value) { return new ExprBoolean(value);}




    default <T2> ETuple2<S,T2> mergeWith(Expr<T2> expr2){
        return new ETuple2<>(this,expr2);
    }
    default <T2,T3> ETuple3<S,T2,T3> mergeWith(Expr<T2> v2, Expr<T3> v3){
        return new ETuple3<>(this,v2,v3);
    }

    <R> R accept(ExprVisitor<R> visitor);

}
