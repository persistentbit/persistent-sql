package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;

/**
 * TODOC
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeObject<T> extends ETypePropertyParent<T>{


	PList<Tuple2<String, Expr<?>>> _all();

	//@Override
	//default <R> EMapper<T, R> mapObject(Function<T, R> mapper) {
	//		return new EMapper<>(this, mapper);
	//}

	@Override
	default String getFullTableName(String schema) {
		return (schema == null ? "" : schema + ".") + _getTableName();
	}

	String _getTableName();

	default ExprValueTable<T> val(T value) {
		return new ExprValueTable<>(this, value);
	}

	PList<Expr<?>> _asExprValues(T value);

	@Override
	default String _toSql(ExprToSqlContext context) {

		return _expand().map(e -> e._toSql(context)).toString(", ");
	}

	@Override
	default String _asParentName(ExprToSqlContext context, String propertyName) {
		if(getParent().isPresent()) {
			return getParent().get()._asParentName(context, propertyName);
		}
		return context.uniqueInstanceName(this, _getTableName()) + "." + propertyName;
	}


}
