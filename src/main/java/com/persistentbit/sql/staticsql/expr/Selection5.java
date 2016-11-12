package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.tuples.Tuple5;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class Selection5<T1, T2, T3, T4, T5> extends BaseSelection<Tuple5<T1, T2, T3, T4, T5>>{

	public final SelectionProperty<T1> col1;
	public final SelectionProperty<T2> col2;
	public final SelectionProperty<T3> col3;
	public final SelectionProperty<T4> col4;
	public final SelectionProperty<T5> col5;


	public Selection5(Query query,
					  Expr<T1> col1,
					  Expr<T2> col2,
					  Expr<T3> col3,
					  Expr<T4> col4,
					  Expr<T5> col5
	) {
		super(query, col1.mergeWith(col2, col3, col4, col5));
		this.col1 = new SelectionProperty<>("col1", col1);
		this.col2 = new SelectionProperty<>("col2", col2);
		this.col3 = new SelectionProperty<>("col3", col3);
		this.col4 = new SelectionProperty<>("col4", col4);
		this.col5 = new SelectionProperty<>("col5", col5);
	}

	@Override
	public PList<Tuple2<String, Expr<?>>> _all() {
		return PList.val(
			Tuple2.of("col1", col1),
			Tuple2.of("col2", col2),
			Tuple2.of("col3", col3),
			Tuple2.of("col4", col4),
			Tuple2.of("col5", col5)
		);
	}

	@Override
	public Tuple5<T1, T2, T3, T4, T5> read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _cache.updatedFromCache(Tuple5.of(
			col1.read(_rowReader, _cache)
			, col2.read(_rowReader, _cache)
			, col3.read(_rowReader, _cache)
			, col4.read(_rowReader, _cache)
			, col5.read(_rowReader, _cache)
		));
	}

	@Override
	public PList<Expr<?>> _expand() {
		return col1._expand().plusAll(col2._expand()).plusAll(col3._expand()).plusAll(col4._expand())
			.plusAll(col5._expand());
	}

	@Override
	public PList<BaseSelection<?>.SelectionProperty<?>> selections() {
		return PList.val(col1, col2, col3, col4, col5);
	}

}