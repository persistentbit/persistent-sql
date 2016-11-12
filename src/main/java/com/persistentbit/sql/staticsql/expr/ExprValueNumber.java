package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents an {@link Expr} that holds a number value
 * @author Peter Muys
 * @since 28/09/16
 */
public class ExprValueNumber<N extends Number> implements ETypeNumber<N>, ETypeValue<N>{

	private       N                       value;
	private final Class<? extends Number> valueClass;

	public ExprValueNumber(Class<? extends Number> valueClass, N value) {
		this.valueClass = valueClass;
		this.value = value;
	}

	@Override
	public String toString() {
		return "" + value;
	}


	public N getValue() {
		return value;
	}

	@Override
	public N read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return (N) _rowReader.readNext(valueClass);
	}

	@Override
	public ETypeValue setValue(N value) {
		this.value = value;
		return this;
	}

	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
		preparedStatement.setObject(parameterIndex, value);
	}

	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return value == null ? "NULL" : "" + value;
	}
}
