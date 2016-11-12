package com.persistentbit.sql.staticsql.expr;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprValueBoolean implements ETypeBoolean, ETypeValue<Boolean>{

	private Boolean value;

	public ExprValueBoolean(Boolean value) {
		this.value = value;
	}


	@Override
	public ETypeValue setValue(Boolean value) {
		this.value = value;
		return this;
	}

	public Boolean getValue() {
		return value;
	}


	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
		if(value == null) {
			preparedStatement.setNull(parameterIndex, Types.BOOLEAN);
		}
		else {
			preparedStatement.setBoolean(parameterIndex, value);
		}
	}

	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return value.toString().toUpperCase();
	}

}
