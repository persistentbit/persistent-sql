package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.mixins.MixinEq;


/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeString extends Expr<String>, MixinEq<ETypeString>{

	default ETypeBoolean eq(String other) {
		return eq(Sql.val(other));
	}

	default ETypeBoolean notEq(String other) {
		return notEq(Sql.val(other));
	}

	default ETypeBoolean notLike(ETypeString other) { return like(other).not();}

	default ETypeBoolean like(ETypeString other) { return new ExprStringLike(this, other);}

	default ETypeBoolean notLike(String other) { return like(other).not();}

	default ETypeBoolean like(String other) { return this.like(Sql.val(other));}

	default ETypeString add(String value) {
		return add(Sql.val(value));
	}

	default ETypeString add(ETypeString expr) {
		return new ExprStringAdd(this, expr);
	}

	@Override
	default String read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _rowReader.readNext(String.class);
	}

	default ETypeBoolean between(Expr<String> left, String right) {
		return between(left, Sql.val(right));
	}

	//***************************  BETWEEN
	default ETypeBoolean between(Expr<String> left, Expr<String> right) {
		return new ExprBetween<>(this, left, right);
	}

	default ETypeBoolean between(String left, Expr<String> right) {
		return between(Sql.val(left), right);
	}

	default ETypeBoolean between(String left, String right) {
		return between(Sql.val(left), Sql.val(right));
	}

	//************************* UPPER / LOWER case

	default ETypeString toUpperCase() {
		return new EStringUpperLower(EStringUpperLower.Type.toUpperCase, this);
	}

	default ETypeString toLowerCase() {
		return new EStringUpperLower(EStringUpperLower.Type.toLowerCase, this);
	}
}
