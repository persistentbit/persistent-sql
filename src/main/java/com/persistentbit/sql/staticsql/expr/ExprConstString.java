package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.expr.ETypeString;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprConstString implements ETypeString {
    private final String value;

    public ExprConstString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    public String getValue() {
        return value;
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        return context.getDbType().asLiteralString(value);
    }

}
