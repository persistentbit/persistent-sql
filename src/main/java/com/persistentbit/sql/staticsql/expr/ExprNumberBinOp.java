package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprNumberBinOp<N extends Number> implements ETypeNumber<N>{

	private ETypeNumber<N> left;
	private ETypeNumber<N> right;
	private String         binOp;

	public ExprNumberBinOp(ETypeNumber<N> left, ETypeNumber<N> right, String binOp) {
		this.left = left;
		this.right = right;
		this.binOp = binOp;
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " " + binOp + " " + right + ")";
	}

	public ETypeNumber<N> getLeft() {
		return left;
	}

	public ETypeNumber<N> getRight() {
		return right;
	}

	public String getBinOp() {
		return binOp;
	}

	@Override
	public N read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return left.read(_rowReader, _cache);
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return left._toSql(context) + " " + binOp + " " + right._toSql(context);
	}


}
