package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprPropertyDate  extends ExprProperty<LocalDate> implements ETypeDate {
    public ExprPropertyDate(Expr parent, String propertyName) {
        super(LocalDate.class,parent,propertyName);
    }
}
