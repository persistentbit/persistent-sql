package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.function.Function3;
import com.persistentbit.core.tuples.Tuple3;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Represent a Tuple of 3 {@link Expr} rendered as a Sql comma separated list
 * @author Peter Muys
 * @since 2/10/16
 */
public class ETuple3<T1, T2, T3> implements Expr<Tuple3<T1, T2, T3>>{

	private final Expr<T1> v1;
	private final Expr<T2> v2;
	private final Expr<T3> v3;

	public ETuple3(Expr<T1> v1, Expr<T2> v2, Expr<T3> v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}

	public <R> Expr<R> map(Function3<T1, T2, T3, R> mapper) {
		return new EMapper<>(this, (t -> t.map(mapper)));
	}


	public Expr<T1> getV1() {
		return v1;
	}

	public Expr<T2> getV2() {
		return v2;
	}

	public Expr<T3> getV3() {
		return v3;
	}

	@Override
	public Tuple3<T1, T2, T3> read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _cache.updatedFromCache(Tuple3.of(
			v1.read(_rowReader, _cache)
			, v2.read(_rowReader, _cache)
			, v3.read(_rowReader, _cache)
		));
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
			.plusAll(v3._expand())
			;
	}
}
