package com.persistentbit.sql.staticsql.expr;

/**
 * Represents an {@link Expr} of type Number (Integer,Short,Long,Float,Double)
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeNumber<N extends Number> extends Expr<N>{


	default ETypeNumber<Integer> asInt() {
		return new ExprNumberCast<>(this, Integer.class);
	}

	default ETypeNumber<Short> asShort() {
		return new ExprNumberCast<>(this, Short.class);
	}

	default ETypeNumber<Long> asLong() {
		return new ExprNumberCast<>(this, Long.class);
	}

	default ETypeNumber<Float> asFloat() {
		return new ExprNumberCast<>(this, Float.class);
	}

	default ETypeNumber<Double> asDouble() {
		return new ExprNumberCast<>(this, Double.class);
	}

	default ETypeString asString() {
		return new ExprNumberToString(this);
	}

	default ETypeBoolean eq(N right) {
		return new ExprCompare<>(this, Sql.val(right), ExprCompare.CompType.eq);
	}

	default ETypeBoolean lt(N right) {
		return new ExprCompare<>(this, Sql.val(right), ExprCompare.CompType.lt);
	}

	default ETypeBoolean ltEq(N right) {
		return new ExprCompare<>(this, Sql.val(right), ExprCompare.CompType.ltEq);
	}

	default ETypeBoolean gt(N right) {
		return new ExprCompare<>(this, Sql.val(right), ExprCompare.CompType.gt);
	}

	default ETypeBoolean gtEq(N right) {
		return new ExprCompare<>(this, Sql.val(right), ExprCompare.CompType.gtEq);
	}

	default ETypeBoolean eq(ETypeNumber<N> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.eq);
	}

	default ETypeBoolean lt(ETypeNumber<N> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.lt);
	}

	default ETypeBoolean ltEq(ETypeNumber<N> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.ltEq);
	}

	default ETypeBoolean gt(ETypeNumber<N> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.gt);
	}

	default ETypeBoolean gtEq(ETypeNumber<N> right) {
		return new ExprCompare<>(this, right, ExprCompare.CompType.gtEq);
	}

	default ETypeNumber<N> add(N number) {
		return add(Sql.val(number));
	}

	default ETypeNumber<N> add(ETypeNumber<N> e) {
		return new ExprNumberBinOp<>(this, e, "+");
	}

	default ETypeNumber<N> sub(N number) {
		return sub(Sql.val(number));
	}

	default ETypeNumber<N> sub(ETypeNumber<N> e) {
		return new ExprNumberBinOp<>(this, e, "-");
	}

	default ETypeNumber<N> mul(N number) {
		return mul(Sql.val(number));
	}

	default ETypeNumber<N> mul(ETypeNumber<N> e) {
		return new ExprNumberBinOp<>(this, e, "*");
	}

	default ETypeNumber<N> div(N number) {
		return div(Sql.val(number));
	}

	default ETypeNumber<N> div(ETypeNumber<N> e) {
		return new ExprNumberBinOp<>(this, e, "/");
	}

	default ETypeBoolean isNull() {
		return new ExprIsNull(this, false);
	}

	default ETypeBoolean isNotNull() {
		return new ExprIsNull(this, false);
	}

	default ETypeBoolean between(Expr<N> left, N right) {
		return between(left, Sql.val(right));
	}

	//***************************  BETWEEN
	default ETypeBoolean between(Expr<N> left, Expr<N> right) {
		return new ExprBetween<>(this, left, right);
	}

	default ETypeBoolean between(N left, Expr<N> right) {
		return between(Sql.val(left), right);
	}

	default ETypeBoolean between(N left, N right) {
		return between(Sql.val(left), Sql.val(right));
	}
}
