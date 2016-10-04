package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;

/**
 * Created by petermuys on 4/10/16.
 */
public interface ETypeDate extends Expr<LocalDate>,MixinEq<ETypeDate>{
    default ETypeBoolean eq(LocalDate date){
        return eq(new ExprDate(date));
    }
    default ETypeBoolean notEq(LocalDate date){
        return notEq(new ExprDate(date));
    }
}
