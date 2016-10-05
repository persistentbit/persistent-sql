package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 5/10/16.
 */
public class EStringGroup extends EGroup<String> implements ETypeString{
    public EStringGroup(Expr<String> value) {
        super(value);
    }
}
