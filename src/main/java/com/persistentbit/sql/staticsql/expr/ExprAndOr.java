package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprAndOr implements ETypeBoolean{

	private final ETypeBoolean left;
	private final ETypeBoolean right;
	private final LogicType    logicType;
	public ExprAndOr(ETypeBoolean left, ETypeBoolean right, LogicType logicType) {
		this.left = left;
		this.right = right;
		this.logicType = logicType;
	}

	@Override
	public String toString() {
		return left + " " + logicType + " " + right;
	}

	public ETypeBoolean getLeft() {
		return left;
	}

	public ETypeBoolean getRight() {
		return right;
	}

	public LogicType getLogicType() {
		return logicType;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return left._toSql(context) + " " + logicType.name().toUpperCase() + " " + right._toSql(context);
	}

	public enum LogicType{
		and, or
	}

}
