package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;

/**
 * Created by petermuys on 5/10/16.
 */
public class ExprPropertyEnum<T extends Enum<?>>  extends ExprProperty<T> implements ETypeEnum<T> {

    public ExprPropertyEnum(Class<T> valueClass, Expr parent, String propertyName) {
        super(valueClass, parent, propertyName);
    }
}
