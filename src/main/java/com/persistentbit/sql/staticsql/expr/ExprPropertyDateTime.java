package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprPropertyDateTime   extends ExprProperty<LocalDateTime> implements ETypeDateTime {
    public ExprPropertyDateTime(Expr parent, String propertyName) {
        super(LocalDateTime.class,parent,propertyName);
    }
}
