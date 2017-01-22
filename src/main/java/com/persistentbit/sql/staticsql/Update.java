package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.sqlwork.DbTransManager;
import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.Sql;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents an Sql Update statement.<br>
 * @author Peter Muys
 * @since 8/10/16
 */
public class Update implements DbWork<Integer>{

	private final ETypeObject                     table;
	private final ETypeBoolean                    where;
	private final PList<Tuple2<Expr<?>, Expr<?>>> set;

	private Update(ETypeObject table, ETypeBoolean where,
				   PList<Tuple2<Expr<?>, Expr<?>>> set
	) {
		this.table = Objects.requireNonNull(table);
		this.where = where;
		this.set = Objects.requireNonNull(set);
	}

	public Update(ETypeObject table) {
		this(table, null, PList.empty());
	}

	public static Update table(ETypeObject table) {
		return new Update(table);
	}

	public <V> Update set(Expr<V> property, Expr<? extends V> value) {
		return new Update(table, where, set.plus(Tuple2.of(property, value)));
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
		return new Update(table, whereExpr, set);
	}

	@Override
	public Result<Integer> execute(DbContext dbc, DbTransManager tm) throws Exception {
		return Result.function(dbc, tm).code(log -> {
			UpdateSqlBuilder b = new UpdateSqlBuilder(dbc, this);
			log.info(b.generateNoParams());

			Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();
			try(PreparedStatement s = tm.get().prepareStatement(generatedQuery._1)) {
				generatedQuery._2.accept(s);
				return Result.success(s.executeUpdate());
			}
		});
	}

	public ETypeObject getTable() {
		return table;
	}

	public Optional<ETypeBoolean> getWhere() {
		return Optional.ofNullable(where);
	}

	public PList<Tuple2<Expr<?>, Expr<?>>> getSet() {
		return set;
	}
}
