package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * A EGroup expression is a {@link Expr} that is translated to sql as '(' value ')'.
 *
 * @author Peter Muys
 * @since 5/10/16
 */
public class EGroup<T> implements Expr<T>{

	protected Expr<T> value;

	public EGroup(Expr<T> value) {
		this.value = value;
	}


	public Expr<T> getValue() {
		return value;
	}

	@Override
	public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return value.read(_rowReader, _cache);
	}


	@Override
	public String _toSql(ExprToSqlContext context) {
		return "(" + value._toSql(context) + ")";
	}

	@Override
	public PList<Expr<?>> _expand() {
		return value._expand();
	}
}
