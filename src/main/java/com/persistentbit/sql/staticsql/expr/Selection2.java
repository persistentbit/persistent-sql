package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.tuples.Tuple3;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class Selection2<T1,T2> extends BaseSelection<Tuple2<T1,T2>> {

    public final Expr<T1> col1;
    public final Expr<T2> col2;

    public Selection2(Query query,
                      Expr<T1> col1,
                      Expr<T2> col2
    ) {
        super(query, col1.mergeWith(col2));
        this.col1 = col1;
        this.col2 = col2;
    }

    @Override
    public PList<Tuple2<String, Expr>> _all() {
        return PList.val(
                Tuple2.of("col1",col1),
                Tuple2.of("col2",col2)
        );
    }
    @Override
    public Tuple2<T1, T2> read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _cache.updatedFromCache(Tuple2.of(
                col1.read(_rowReader,_cache)
                ,col2.read(_rowReader,_cache)
        ));
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        return
    }
}
