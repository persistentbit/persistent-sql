package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * Created by petermuys on 8/10/16.
 */
public class Update{

	private DbSql        db;
	private ETypeObject  table;
	private ETypeBoolean where;
	private PList<Tuple2<Expr<?>, Expr<?>>> set = PList.empty();


	public Update(DbSql db, ETypeObject table) {
		this.db = db;
		this.table = table;
	}

	public <V> Update set(Expr<V> property, Expr<V> value) {
		set = set.plus(Tuple2.of(property, value));
		return this;
	}


	public Update where(ETypeBoolean whereExpr) {
		this.where = whereExpr;
		return this;
	}

	public int execute() {
		return db.run(this);
	}


	public ETypeObject getTable() {
		return table;
	}

	public ETypeBoolean getWhere() {
		return where;
	}

	public PList<Tuple2<Expr<?>, Expr<?>>> getSet() {
		return set;
	}
}
