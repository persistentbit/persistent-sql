package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprBoolean implements ETypeBoolean{
    private final Boolean value;

    public ExprBoolean(Boolean value) {
        this.value = value;
    }
}
