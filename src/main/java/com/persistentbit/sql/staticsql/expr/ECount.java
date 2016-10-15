package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 13:42
 */
public class ECount implements ETypeNumber<Long>{
    private Expr<?> countWhat;
    private boolean distinct;

    public ECount(Expr<?> countWhat, boolean distinct) {
        this.countWhat = countWhat;
        this.distinct = distinct;
    }

    @Override
    public Long read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(Long.class);
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        String what = "*";
        if(countWhat != null){
            what = countWhat._toSql(context);
        }
        if(distinct){
            what = "DISTINCT " + what;
        }
        return "COUNT(" + what + ")";
    }


}
