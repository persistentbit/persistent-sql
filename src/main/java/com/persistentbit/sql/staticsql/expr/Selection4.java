package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.tuples.Tuple4;
import com.persistentbit.core.tuples.Tuple5;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class Selection4<T1,T2,T3,T4> extends BaseSelection<Tuple4<T1,T2,T3,T4>> {

    public final Expr<T1> col1;
    public final Expr<T2> col2;
    public final Expr<T3> col3;
    public final Expr<T4> col4;


    public Selection4(Query query,
                      Expr<T1> col1,
                      Expr<T2> col2,
                      Expr<T3> col3,
                      Expr<T4> col4
    ) {
        super(query, col1.mergeWith(col2,col3,col4));
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
    }

    @Override
    public PList<Tuple2<String, Expr>> _all() {
        return PList.val(
                Tuple2.of("col1",col1),
                Tuple2.of("col2",col2),
                Tuple2.of("col3",col3),
                Tuple2.of("col4",col4)
        );
    }
    @Override
    public Tuple4<T1, T2,T3,T4> read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _cache.updatedFromCache(Tuple4.of(
                col1.read(_rowReader,_cache)
                ,col2.read(_rowReader,_cache)
                ,col3.read(_rowReader,_cache)
                ,col4.read(_rowReader,_cache)
        ));
    }
}