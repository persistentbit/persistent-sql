package com.persistentbit.sql.staticsql;

import com.persistentbit.core.utils.BaseValueClass;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 1/10/16.
 */
public class Join extends BaseValueClass{

	private final Query       query;
	private final ETypeObject table;
	private final Type        type;
	private final Expr        joinExpr;
	public Join(Query query, Type type, ETypeObject table) {
		this(query, type, table, null);
	}


	public Join(Query query, Type type, ETypeObject table, Expr joinExpr) {
		this.query = query;
		this.type = type;
		this.table = table;
		this.joinExpr = joinExpr;
	}

	public Query on(Expr joinExpr) {
		Join n = new Join(query, type, table, joinExpr);
		return n.query();
	}

	public Query query() {
		return query.addJoin(this);
	}

	public ETypeObject getTable() {
		return table;
	}

	public Optional<Expr> getJoinExpr() {
		return Optional.ofNullable(joinExpr);
	}

	public Type getType() {
		return type;
	}

	public enum Type{
		inner, left, right, full
	}
}
