package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.staticsql.expr.EGroup;
import com.persistentbit.sql.staticsql.expr.ETypeNumber;
import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * Created by petermuys on 5/10/16.
 */
public class ENumberGroup<T extends Number> extends EGroup<T> implements ETypeNumber<T>{

	public ENumberGroup(Expr<T> value) {
		super(value);
	}
}
