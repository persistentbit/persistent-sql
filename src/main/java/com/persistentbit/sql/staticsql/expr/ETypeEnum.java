package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 5/10/16.
 */
public interface ETypeEnum<T extends Enum<?>> extends Expr<T>,MixinEq<ETypeEnum<T>>{
    default ETypeBoolean eq(T value){
        return eq(Expr.val(value));
    }
}
