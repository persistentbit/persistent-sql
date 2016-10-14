package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.NotYet;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;



/**
 * Created by petermuys on 14/10/16.
 */
public class Selection1<T1> extends BaseSelection<T1>{

    public final Expr<T1> col1;

    class PropertyExpr<P> implements Expr<P>{
        private Expr<P> value;

        public PropertyExpr(Expr<P> value) {
            this.value = value;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            throw new NotYet();
        }

        @Override
        public P read(RowReader _rowReader, ExprRowReaderCache _cache) {
            return value.read(_rowReader,_cache);
        }

        @Override
        public String _toSql(ExprToSqlContext context) {
            return context.uniqueInstanceName(Selection1.this,"sel") + "." + ;
        }
    }

    public Selection1(Query query, Expr<T1> col1) {
        super(query, col1);
        this.col1 = col1;
    }

    @Override
    public PList<Tuple2<String, Expr>> _all() {
        return PList.val(Tuple2.of("col1",col1));
    }

    @Override
    public T1 read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return col1.read(_rowReader,_cache);
    }


}
