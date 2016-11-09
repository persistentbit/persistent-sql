package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.function.Function2;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.ETypeSelection;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a DB instance
 *
 * @author Peter Muys
 * @since 3/10/16
 */
public class DbSql{

	private static final PLog log = PLog.get(DbSql.class);
	public final  TransactionRunner run;
	private final DbType            dbType;
	private final String            schema;

	public DbSql(DbType dbType, TransactionRunner run) {
		this(dbType, run, null);
	}

	public DbSql(DbType dbType, TransactionRunner run, String schema) {
		this.dbType = dbType;
		this.run = run;
		this.schema = schema;
	}

	/**
	 * Get the schema name
	 *
	 * @return The Optional schema name
	 */
	public Optional<String> getSchema() {
		return Optional.ofNullable(schema);
	}


	/**
	 * Create a new Sql Query object for the given Table
	 *
	 * @param typeObject The query root table or view
	 *
	 * @return The Query object
	 */
	public Query queryFrom(ETypeObject typeObject) {
		return Query.from(this, typeObject);
	}

	/**
	 * Create a new Sql Update object for the given Table
	 *
	 * @param typeObject The table to update
	 *
	 * @return The Update object
	 */
	public Update update(ETypeObject typeObject) { return new Update(this, typeObject); }

	public Delete deleteFrom(ETypeObject typeObject) { return new Delete(this, typeObject); }

	public int runInsert(ETypeObject table, Expr... values) {
		Insert insert = Insert.into(table, values);
		return run(insert);
	}

	public int run(Insert insert) {
		InsertSqlBuilder b = new InsertSqlBuilder(dbType, schema, insert);

		log.debug(b::generateNoParams);

		Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();

		return run.trans(c -> {
			PreparedStatement s = c.prepareStatement(generatedQuery._1);
			generatedQuery._2.accept(s);
			return s.executeUpdate();
		});

	}

	public <T, K, R> R runInsertWithGenKeys(ETypeObject<T> table, T value, Expr<K> generatedKey,
											Function2<T, K, R> mapper
	) {
		K key = runInsertWithGenKeys(table, value, generatedKey);
		return mapper.apply(value, key);
	}

	public <T, K> K runInsertWithGenKeys(ETypeObject<T> table, T value, Expr<K> generatedKey) {
		return run(Insert.into(table, table.val(value)).withGeneratedKeys(generatedKey));
	}

	public <T> T run(InsertWithGeneratedKeys<T> ik) {
		InsertSqlBuilder b   = new InsertSqlBuilder(dbType, schema, ik.getInsert(), ik.getGenerated());

		log.debug(b::generateNoParams);

		Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();

		return run.trans(c -> {
			PreparedStatement s = c.prepareStatement(generatedQuery._1, Statement.RETURN_GENERATED_KEYS);

			generatedQuery._2.accept(s);

			int               count      = s.executeUpdate();
			ExprRowReader     exprReader = new ExprRowReader();


			try(ResultSet generatedKeys = s.getGeneratedKeys()) {
				ResultSetRowReader rowReader = new ResultSetRowReader(generatedKeys);
				if(generatedKeys.next()) {
					return exprReader.read(ik.getGenerated(), rowReader);
				}
				throw new RuntimeException("No generated keys...");
			}
		});
	}

	public <T> PList<T> run(ETypeSelection<T> selection) {
		QuerySqlBuilder b = new QuerySqlBuilder(selection, dbType, schema);

		log.debug(b::generateNoParams);

		Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();

		return run.trans(c -> {
			PreparedStatement s = c.prepareStatement(generatedQuery._1);
			generatedQuery._2.accept(s);
			ExprRowReader exprReader = new ExprRowReader();
			try(ResultSet rs = s.executeQuery()) {
				ResultSetRowReader rowReader = new ResultSetRowReader(rs);
				PList<T>           res       = PList.empty();
				while(rs.next()) {
					res = res.plus(selection.read(rowReader, exprReader));
					rowReader.nextRow();
				}
				return res;
			}
		});
	}

	public int run(Update update) {
		UpdateSqlBuilder b = new UpdateSqlBuilder(dbType, schema, update);

		log.debug(b::generateNoParams);

		Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();

		return run.trans(c -> {
			PreparedStatement s = c.prepareStatement(generatedQuery._1);
			generatedQuery._2.accept(s);
			return s.executeUpdate();
		});
	}

	public int run(Delete delete) {
		DeleteSqlBuilder b   = new DeleteSqlBuilder(dbType, schema, delete);
		log.debug(b::generateNoParams);

		Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();

		return run.trans(c -> {
			PreparedStatement s = c.prepareStatement(generatedQuery._1);
			generatedQuery._2.accept(s);
			return s.executeUpdate();
		});
	}
}
