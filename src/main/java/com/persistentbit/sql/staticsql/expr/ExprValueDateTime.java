package com.persistentbit.sql.staticsql.expr;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Represents an {@link Expr} that holds a {@link LocalDateTime} value
 * @author Peter Muys
 * @since 4/10/16
 */
public class ExprValueDateTime implements Expr<LocalDateTime>, ETypeDateTime, ETypeValue<LocalDateTime>{

	private LocalDateTime value;

	public ExprValueDateTime(LocalDateTime value) {
		this.value = value;
	}

	public LocalDateTime getValue() {
		return value;
	}



	@Override
	public ETypeValue setValue(LocalDateTime value) {
		this.value = value;
		return this;
	}

	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
		preparedStatement.setTimestamp(parameterIndex, value == null ? null : Timestamp.valueOf(value));
	}

	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return value == null ? "NULL" : context.getDbType().asLiteralDateTime(value);
	}
}
