package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * A Boolean {@link Expr} type with default methods
 *
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeBoolean extends Expr<Boolean>{

	default ETypeBoolean eq(boolean right) {
		return eq(Sql.val(right));
	}

	default ETypeBoolean eq(Expr<Boolean> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.eq);
	}

	default ETypeBoolean notEq(boolean right) {
		return notEq(Sql.val(right));
	}

	default ETypeBoolean notEq(Expr<Boolean> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.neq);
	}

	default ETypeBoolean not() {
		return new ExprNot(this);
	}

	default ETypeBoolean and(ETypeBoolean right) {
		return new ExprAndOr(this, right, ExprAndOr.LogicType.and);
	}

	default ETypeBoolean or(ETypeBoolean right) {
		return new ExprAndOr(this, right, ExprAndOr.LogicType.or);
	}

	@Override
	default Boolean read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _rowReader.readNext(Boolean.class);
	}
}
