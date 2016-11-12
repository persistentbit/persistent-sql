package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an Sql Update statement.<br>
 * @author Peter Muys
 * @since 8/10/16
 */
public class Update{

	private final DbSql        db;
	private final ETypeObject  table;
	private       ETypeBoolean where;
	private PList<Tuple2<Expr<?>, Expr<?>>> set = PList.empty();


	public Update(DbSql db, ETypeObject table) {
		this.db = db;
		this.table = table;
	}

	public <V> Update set(Expr<V> property, Expr<? extends V> value) {
		set = set.plus(Tuple2.of(property, value));
		return this;
	}

	public Update set(Expr<Number> property, Number value) {
		return set(property, Sql.val(value));
	}

	public Update set(Expr<String> property, String value) {
		return set(property, Sql.val(value));
	}

	public Update set(Expr<LocalDate> property, LocalDate value) {
		return set(property, Sql.val(value));
	}

	public Update set(Expr<LocalDateTime> property, LocalDateTime value) {
		return set(property, Sql.val(value));
	}

	public Update set(Expr<Boolean> property, Boolean value) {
		return set(property, Sql.val(value));
	}

	public <V extends Enum<V>> Update set(Expr<V> property, V value) {
		return set(property, Sql.val(value));
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
