package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;

/**
 * Represents an SQL delete statement
 *
 * @author Peter Muys
 * @since 13/10/2016
 */
public class Delete{

	private DbSql        db;
	private ETypeObject  table;
	private ETypeBoolean where;


	public Delete(DbSql db, ETypeObject table) {
		this.db = db;
		this.table = table;
	}


	public Delete where(ETypeBoolean whereExpr) {
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


}
