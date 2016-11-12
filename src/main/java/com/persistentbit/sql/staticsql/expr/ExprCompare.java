package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprCompare<T extends Expr> implements ETypeBoolean{

	private final T        left;
	private final T        right;
	private final CompType compType;
	public ExprCompare(T left, T right, CompType compType) {
		this.left = left;
		this.right = right;
		this.compType = compType;
	}

	@Override
	public String toString() {
		return left.toString() + compType + right.toString();
	}

	public T getLeft() {
		return left;
	}

	public T getRight() {
		return right;
	}

	public CompType getCompType() {
		return compType;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return left._toSql(context) + " " + compType + " " + right._toSql(context);
	}

	public enum CompType{
		eq("="), lt("<"), gt(">"), ltEq("<="), gtEq(">="), neq("<>");
		private String token;

		CompType(String token) {
			this.token = token;
		}

		@Override
		public String toString() {
			return token;
		}
	}


}
