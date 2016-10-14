package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.tuples.Tuple6;
import com.persistentbit.core.tuples.Tuple7;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class Selection6 <T1,T2,T3,T4,T5,T6> extends BaseSelection<Tuple6<T1,T2,T3,T4,T5,T6>> {

    public final Expr<T1> col1;
    public final Expr<T2> col2;
    public final Expr<T3> col3;
    public final Expr<T4> col4;
    public final Expr<T5> col5;
    public final Expr<T6> col6;

    public Selection6(Query query,
                      Expr<T1> col1,
                      Expr<T2> col2,
                      Expr<T3> col3,
                      Expr<T4> col4,
                      Expr<T5> col5,
                      Expr<T6> col6
    ) {
        super(query, col1.mergeWith(col2,col3,col4,col5,col6));
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
        this.col6 = col6;
    }

    @Override
    public PList<Tuple2<String, Expr>> _all() {
        return PList.val(
                Tuple2.of("col1",col1),
                Tuple2.of("col2",col2),
                Tuple2.of("col3",col3),
                Tuple2.of("col4",col4),
                Tuple2.of("col5",col5),
                Tuple2.of("col6",col6)
        );
    }
    @Override
    public Tuple6<T1, T2,T3,T4,T5,T6> read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _cache.updatedFromCache(Tuple6.of(
                col1.read(_rowReader,_cache)
                ,col2.read(_rowReader,_cache)
                ,col3.read(_rowReader,_cache)
                ,col4.read(_rowReader,_cache)
                ,col5.read(_rowReader,_cache)
                ,col6.read(_rowReader,_cache)
        ));
    }
}
