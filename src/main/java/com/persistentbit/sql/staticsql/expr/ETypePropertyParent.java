package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.exceptions.ToDo;

import java.util.Optional;

/**
 * TODOC
 * @author Peter Muys
 * @since 14/10/16
 */
public interface ETypePropertyParent<T> extends Expr<T>{

	String _asParentName(ExprToSqlContext context, String propertyName);

	Optional<ETypePropertyParent> getParent();

	String getFullTableName(String schema);

	default ETypeObject<T> withNewParent(ETypePropertyParent newParent) {
		throw new ToDo();
	}
}
