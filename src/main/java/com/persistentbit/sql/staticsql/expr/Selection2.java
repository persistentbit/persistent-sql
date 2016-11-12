package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class Selection2<T1, T2> extends BaseSelection<Tuple2<T1, T2>>{

	public final SelectionProperty<T1> col1;
	public final SelectionProperty<T2> col2;

	public Selection2(Query query,
					  Expr<T1> col1,
					  Expr<T2> col2
	) {
		super(query, col1.mergeWith(col2));
		this.col1 = new SelectionProperty<>("col1", col1);
		this.col2 = new SelectionProperty<>("col2", col2);
	}

	@Override
	public PList<Tuple2<String, Expr<?>>> _all() {
		return PList.val(
			Tuple2.of("col1", col1),
			Tuple2.of("col2", col2)
		);
	}

	@Override
	public Tuple2<T1, T2> read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _cache.updatedFromCache(Tuple2.of(
			col1.read(_rowReader, _cache)
			, col2.read(_rowReader, _cache)
		));
	}

	@Override
	public PList<Expr<?>> _expand() {
		return col1._expand().plusAll(col2._expand());
	}

	@Override
	public PList<BaseSelection<?>.SelectionProperty<?>> selections() {
		return PList.val(col1, col2);
	}


}
