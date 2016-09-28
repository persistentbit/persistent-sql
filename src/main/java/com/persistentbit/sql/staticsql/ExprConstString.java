package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprConstString implements ETypeString{
    private final String value;

    public ExprConstString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
