package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.exceptions.RtSqlException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An ETypeValue is an {@link Expr} that contains a literal value.<br>
 * When generating Sql, these expression will be converted to Jdbc parameters
 *
 * @author petermuys
 * @since 10/11/16
 */
public interface ETypeValue<T> extends Expr<T>{

	ETypeValue setValue(T value);

	T getValue();

	@Override
	default String _toSql(ExprToSqlContext context) {
		if(context.isUsingSqlParameters()) {
			context.setParam((t) -> {
				try {
					_setParam(t._1, t._2);
				} catch(SQLException e) {
					RtSqlException.map(e);
				}
			});
			return "?";
		}
		return _literalValueToSql(context);
	}


	void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException;

	String _literalValueToSql(ExprToSqlContext context);
}
