package com.persistentbit.sql.staticsql.expr;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Represents an {@link Expr} that holds a LocalDate
 * @author Peter Muys
 * @since 4/10/16
 */
public class ExprValueDate implements Expr<LocalDate>, ETypeDate, ETypeValue<LocalDate>{

	private LocalDate value;

	public ExprValueDate(LocalDate value) {

		this.value = value;
	}

	@Override
	public ETypeValue setValue(LocalDate value) {
		this.value = value;
		return this;
	}

	public LocalDate getValue() {
		return value;
	}

	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
		preparedStatement.setDate(parameterIndex, value == null ? null : Date.valueOf(value));

	}


	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return value == null ? "NULL" : context.getDbType().asLiteralDate(value);
	}


}
