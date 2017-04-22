package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.exceptions.ToDo;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;


/**
 * Represent a SQL selection of 1 value.
 * @since 14/10/16
 * @author Peter Muys
 */
public class Selection1<T1> extends BaseSelection<T1>{

	public final SelectionProperty<T1> col1;

	public Selection1(Query query, Expr<T1> col1) {
		super(query, col1);
		this.col1 = new SelectionProperty<>("col1", col1);
	}

	@Override
	public PList<Tuple2<String, Expr<?>>> _all() {
		return PList.val(Tuple2.of("col1", col1));
	}

	@Override
	public T1 read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return col1.read(_rowReader, _cache);
	}

	@Override
	public PList<Expr<?>> _asExprValues(T1 value) {
		throw new ToDo();
	}

	@Override
	public PList<Expr<?>> _expand() {
		return col1._expand();
	}

	@Override
	public PList<BaseSelection<?>.SelectionProperty<?>> selections() {
		return PList.val(col1);
	}
}
