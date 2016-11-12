package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprStringAdd implements ETypeString{

	private ETypeString left;
	private ETypeString right;

	public ExprStringAdd(ETypeString left, ETypeString right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return left + "+" + right;
	}


	public ETypeString getLeft() {
		return left;
	}

	public ETypeString getRight() {
		return right;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return context.getDbType().concatStrings(left._toSql(context), right._toSql(context));
	}


}
