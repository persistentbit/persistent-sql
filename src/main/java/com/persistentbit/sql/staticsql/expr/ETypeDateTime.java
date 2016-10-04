package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public interface ETypeDateTime  extends Expr<LocalDateTime>, MixinEq<ETypeDateTime>{

    default ETypeBoolean eq(LocalDateTime dateTime){
        return eq(Expr.val(dateTime));
    }
    default ETypeBoolean notEq(LocalDateTime dateTime){
        return notEq(Expr.val(dateTime));
    }

}
