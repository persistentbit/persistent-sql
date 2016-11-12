package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.tuples.Tuple7;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class Selection7<T1, T2, T3, T4, T5, T6, T7> extends BaseSelection<Tuple7<T1, T2, T3, T4, T5, T6, T7>>{

	private final SelectionProperty<T1> col1;
	private final SelectionProperty<T2> col2;
	private final SelectionProperty<T3> col3;
	private final SelectionProperty<T4> col4;
	private final SelectionProperty<T5> col5;
	private final SelectionProperty<T6> col6;
	private final SelectionProperty<T7> col7;

	public Selection7(Query query,
					  Expr<T1> col1,
					  Expr<T2> col2,
					  Expr<T3> col3,
					  Expr<T4> col4,
					  Expr<T5> col5,
					  Expr<T6> col6,
					  Expr<T7> col7
	) {
		super(query, col1.mergeWith(col2, col3, col4, col5, col6, col7));
		this.col1 = new SelectionProperty<>("col1", col1);
		this.col2 = new SelectionProperty<>("col2", col2);
		this.col3 = new SelectionProperty<>("col3", col3);
		this.col4 = new SelectionProperty<>("col4", col4);
		this.col5 = new SelectionProperty<>("col5", col5);
		this.col6 = new SelectionProperty<>("col6", col6);
		this.col7 = new SelectionProperty<>("col7", col7);
	}

	@Override
	public PList<Tuple2<String, Expr<?>>> _all() {
		return PList.val(
			Tuple2.of("col1", col1),
			Tuple2.of("col2", col2),
			Tuple2.of("col3", col3),
			Tuple2.of("col4", col4),
			Tuple2.of("col5", col5),
			Tuple2.of("col6", col6),
			Tuple2.of("col7", col7)
		);
	}

	@Override
	public Tuple7<T1, T2, T3, T4, T5, T6, T7> read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _cache.updatedFromCache(Tuple7.of(
			col1.read(_rowReader, _cache)
			, col2.read(_rowReader, _cache)
			, col3.read(_rowReader, _cache)
			, col4.read(_rowReader, _cache)
			, col5.read(_rowReader, _cache)
			, col6.read(_rowReader, _cache)
			, col7.read(_rowReader, _cache)
		));
	}

	@Override
	public PList<Expr<?>> _expand() {
		return col1._expand().plusAll(col2._expand()).plusAll(col3._expand()).plusAll(col4._expand())
			.plusAll(col5._expand()).plusAll(col6._expand()).plusAll(col7._expand());
	}

	@Override
	public PList<BaseSelection<?>.SelectionProperty<?>> selections() {
		return PList.val(col1, col2, col3, col4, col5, col6, col7);
	}

}
