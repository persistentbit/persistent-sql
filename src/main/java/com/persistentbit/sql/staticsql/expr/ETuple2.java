package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.function.Function2;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Represent a Tuple of 2 {@link Expr} rendered as a Sql comma separated list
 * @author Peter Muys
 * @since 2/10/16
 */
public class ETuple2<T1, T2> implements Expr<Tuple2<T1, T2>>{

	private final Expr<T1> v1;
	private final Expr<T2> v2;

	public ETuple2(Expr<T1> v1, Expr<T2> v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	public <T3> ETuple3<T1, T2, T3> add(Expr<T3> expr) {
		return new ETuple3<>(v1, v2, expr);
	}

	public <R> Expr<R> map(Function2<T1, T2, R> mapper) {
		return new EMapper<>(this, (t -> t.map(mapper)));
	}


	public Expr<T1> getV1() {
		return v1;
	}

	public Expr<T2> getV2() {
		return v2;
	}

	@Override
	public Tuple2<T1, T2> read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _cache.updatedFromCache(Tuple2.of(v1.read(_rowReader, _cache), v2.read(_rowReader, _cache)));
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return _expand().map(e -> e._toSql(context)).toString(", ");
	}

	@Override
	public PList<Expr<?>> _expand() {
		return PList.<Expr<?>>empty()
			.plusAll(v1._expand())
			.plusAll(v2._expand())
			;
	}
}
