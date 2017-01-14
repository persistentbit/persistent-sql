package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.ExprToSqlContext;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder for SQL delete statements.<br>
 *
 * @author Peter Muys
 * @since 13/10/2016
 */
class DeleteSqlBuilder{

	private final DbContext                    dbContext;
	private final Delete                       delete;
	private final PMap<ETypeObject, TableInst> tables;

	public DeleteSqlBuilder(DbContext dbContext, Delete delete) {
		this.dbContext = dbContext;
		this.delete = delete;
		PMap<ETypeObject, TableInst> allUsed = PMap.empty();
		allUsed.put(delete.getTable(), new TableInst(delete.getTable().getFullTableName(dbContext.getSchemaName()
																							.orElse(null)), delete
														 .getTable()));
		tables = allUsed;
	}

	private Optional<String> getTableInstance(ETypeObject obj) {
		return tables.getOpt(obj).map(TableInst::getName);
	}

	public Tuple2<String, Consumer<PreparedStatement>> generate() {
		ExprToSqlContext context = new ExprToSqlContext(dbContext, true);
		return Tuple2.of(generate(context), prepStat ->
			context.getParamSetters().zipWithIndex().forEach(t -> t._2.accept(Tuple2.of(prepStat, t._1 + 1)))
		);
	}

	public String generateNoParams() {
		return generate(new ExprToSqlContext(dbContext, false));
	}

	private String generate(ExprToSqlContext context) {
		String           nl      = "\r\n";

		String res = "DELETE FROM  " + delete.getTable().getFullTableName(dbContext.getSchemaName().orElse(null))
			+ " " + context.uniqueInstanceName(delete.getTable(), delete.getTable()._getTableName())
			+ nl;
		if(delete.getWhere() != null) {

			res += nl + " WHERE " + delete.getWhere()._toSql(context);
		}

		return res;
	}

}
