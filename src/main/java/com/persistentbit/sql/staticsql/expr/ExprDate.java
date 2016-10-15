package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;

import java.time.LocalDate;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprDate implements Expr<LocalDate>,ETypeDate{
    private final LocalDate value;

    public LocalDate getValue() {
        return value;
    }

    public ExprDate(LocalDate value) {

        this.value = value;
    }



    @Override
    public String _toSql(ExprToSqlContext context) {
        return context.getDbType().asLiteralDate(value);
    }

    @Override
    public PList<Expr> _expand() {
        return PList.val(this);
    }
}
