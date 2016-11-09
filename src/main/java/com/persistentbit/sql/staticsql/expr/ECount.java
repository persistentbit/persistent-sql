package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Represents a Sql count function:<br>
 * 	COUNT(DISTINCT? what)
 * @author Peter Muys
 * @since 15/10/16
 */
public class ECount implements ETypeNumber<Long>{

	private final Expr<?> countWhat;
	private final boolean distinct;

	@SuppressWarnings("BooleanParameter")
	public ECount(Expr<?> countWhat, boolean distinct) {
		this.countWhat = countWhat;
		this.distinct = distinct;
	}

	@Override
	public Long read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _rowReader.readNext(Long.class);
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		String what = "*";
		if(countWhat != null) {
			what = countWhat._toSql(context);
		}
		if(distinct) {
			what = "DISTINCT " + what;
		}
		return "COUNT(" + what + ")";
	}


}
