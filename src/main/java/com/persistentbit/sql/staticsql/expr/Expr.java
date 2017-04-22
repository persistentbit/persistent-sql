package com.persistentbit.sql.staticsql.expr;


import com.persistentbit.core.collections.PList;
import com.persistentbit.core.exceptions.ToDo;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.function.Function;

/**
 * An Expr instance represents a Java Equivalent of a Sql Expression.<br>
 *
 * @author Peter Muys
 */
public interface Expr<S>{


	// **********************  COUNT FUNCTION *************

	/**
	 * Create an Sql count(...) expression
	 *
	 * @return the Sql expression
	 *
	 * @see #countDistinct()
	 */
	default ETypeNumber<Long> count() {
		return new ECount(this, false);
	}

	/**
	 * Create an Sql count(DISTINCT ...) expression
	 *
	 * @return the Sql expression
	 *
	 * @see #count()
	 */
	default ETypeNumber<Long> countDistinct() {
		return new ECount(this, false);
	}

	/**
	 * an Sql IN expression.
	 *
	 * @param in A ETypeList that can be a fixed list or maybe a select statement
	 *
	 * @return The Boolean sql result
	 */
	default ETypeBoolean in(ETypeList<S> in) {
		return new ExprIn<>(this, in);
	}

	//*******************    MERGE: merge multiple values into 1 Expression

	/**
	 * Merge this value with 1 other value in a Tuple
	 *
	 * @param v2   value 2
	 * @param <T2> value 2 type
	 *
	 * @return the merged expression
	 */

	default <T2> ETuple2<S, T2> mergeWith(Expr<T2> v2) {
		return new ETuple2<>(this, v2);
	}

	/**
	 * Merge this value with 2 other values in a Tuple
	 *
	 * @param v2   value 2
	 * @param v3   value 3
	 * @param <T2> value 2 type
	 * @param <T3> value 3 type
	 *
	 * @return the merged expression
	 */
	default <T2, T3> ETuple3<S, T2, T3> mergeWith(Expr<T2> v2, Expr<T3> v3) {
		return new ETuple3<>(this, v2, v3);
	}

	/**
	 * Merge this value with 3 other values in a Tuple
	 *
	 * @param v2   value 2
	 * @param v3   value 3
	 * @param v4   value 4
	 * @param <T2> value 2 type
	 * @param <T3> value 3 type
	 * @param <T4> value 4 type
	 *
	 * @return the merged expression
	 */
	default <T2, T3, T4> ETuple4<S, T2, T3, T4> mergeWith(Expr<T2> v2, Expr<T3> v3, Expr<T4> v4) {
		return new ETuple4<>(this, v2, v3, v4);
	}

	/**
	 * Merge this value with 4 other values in a Tuple
	 *
	 * @param v2   value 2
	 * @param v3   value 3
	 * @param v4   value 4
	 * @param v5   value 5
	 * @param <T2> value 2 type
	 * @param <T3> value 3 type
	 * @param <T4> value 4 type
	 * @param <T5> value 5 type
	 *
	 * @return the merged expression
	 */
	default <T2, T3, T4, T5> ETuple5<S, T2, T3, T4, T5> mergeWith(
		Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5
	) {
		return new ETuple5<>(this, v2, v3, v4, v5);
	}

	/**
	 * Merge this value with 5 other values in a Tuple
	 *
	 * @param v2   value 2
	 * @param v3   value 3
	 * @param v4   value 4
	 * @param v5   value 5
	 * @param v6   value 6
	 * @param <T2> value 2 type
	 * @param <T3> value 3 type
	 * @param <T4> value 4 type
	 * @param <T5> value 5 type
	 * @param <T6> value 6 type
	 *
	 * @return the merged expression
	 */
	default <T2, T3, T4, T5, T6> ETuple6<S, T2, T3, T4, T5, T6> mergeWith(
		Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5, Expr<T6> v6
	) {
		return new ETuple6<>(this, v2, v3, v4, v5, v6);
	}

	/**
	 * Merge this value with 6 other values in a Tuple
	 *
	 * @param v2   value 2
	 * @param v3   value 3
	 * @param v4   value 4
	 * @param v5   value 5
	 * @param v6   value 6
	 * @param v7   value 7
	 * @param <T2> value 2 type
	 * @param <T3> value 3 type
	 * @param <T4> value 4 type
	 * @param <T5> value 5 type
	 * @param <T6> value 6 type
	 * @param <T7> value 7 type
	 *
	 * @return the merged expression
	 */
	default <T2, T3, T4, T5, T6, T7> ETuple7<S, T2, T3, T4, T5, T6, T7> mergeWith(
		Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5, Expr<T6> v6, Expr<T7> v7
	) {
		return new ETuple7<>(this, v2, v3, v4, v5, v6, v7);
	}


	default <R> EMapper<S, R> mapObject(Function<S, R> mapper) {
		return new EMapper<>(this, mapper);
	}


	S read(RowReader _rowReader, ExprRowReaderCache _cache);

	/**
	 * Return the name of the expression without the tablename.<br>
	 * Mainly used by the insert sql generator to get a list of all column names to insert.<br>
	 *
	 * @param context The context
	 *
	 * @return The full property name without the
	 */
	default String _fullColumnName(ExprToSqlContext context) {
		throw new ToDo("Not Yet supported on " + getClass().getName());
	}

	String _toSql(ExprToSqlContext context);

	default PList<Expr<?>> _expand() {
		return PList.val(this);
	}
}
