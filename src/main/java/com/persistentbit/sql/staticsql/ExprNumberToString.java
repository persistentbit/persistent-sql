package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprNumberToString implements ETypeString{
    private ETypeNumber number;

    public ExprNumberToString(ETypeNumber number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "((String)" + number + ")";
    }
}
