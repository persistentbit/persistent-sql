package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An Sql Expression that wraps al the Values of a ETypeObject
 *
 * @author Peter Muys
 * @since 2/10/16.
 */
public class ExprValueTable<T> implements Expr<T>, ETypeValue<T>{

	private final ETypeObject<T> table;
	private       T              value;

	public ExprValueTable(ETypeObject<T> table, T value) {
		this.table = table;
		this.value = value;
	}


	public ETypeObject<T> getTable() {
		return table;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return table.read(_rowReader, _cache);
	}

	@Override
	public PList<Expr<?>> _expand() {
		return table._asExprValues(value);
	}

	@Override
	public ETypeValue setValue(T value) {
		this.value = value;
		return this;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return _expand().map(e -> e._toSql(context)).toString(", ");
	}


	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
	}

	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return _expand().map(e -> _toSql(context)).toString(", ");
	}
}
