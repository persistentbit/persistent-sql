package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
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
public class DeleteSqlBuilder{

	private final DbType dbType;
	private final String schema;

	private final Delete                       delete;
	private final PMap<ETypeObject, TableInst> tables;

	public DeleteSqlBuilder(DbType dbType, String schema, Delete delete) {
		this.dbType = dbType;
		this.schema = schema;
		this.delete = delete;
		PMap<ETypeObject, TableInst> allUsed = PMap.empty();
		allUsed.put(delete.getTable(), new TableInst(delete.getTable().getFullTableName(schema), delete.getTable()));
		tables = allUsed;
	}

	private Optional<String> getTableInstance(ETypeObject obj) {
		return tables.getOpt(obj).map(TableInst::getName);
	}

	public Tuple2<String, Consumer<PreparedStatement>> generate() {
		ExprToSqlContext context = new ExprToSqlContext(dbType, schema, true);
		return Tuple2.of(generate(context), prepStat ->
			context.getParamSetters().zipWithIndex().forEach(t -> t._2.accept(Tuple2.of(prepStat, t._1 + 1)))
		);
	}

	public String generateNoParams() {
		return generate(new ExprToSqlContext(dbType, schema, false));
	}

	private String generate(ExprToSqlContext context) {
		String           nl      = "\r\n";

		String res = "DELETE FROM  " + delete.getTable().getFullTableName(schema)
			+ " " + context.uniqueInstanceName(delete.getTable(), delete.getTable()._getTableName())
			+ nl;
		if(delete.getWhere() != null) {

			res += nl + " WHERE " + delete.getWhere()._toSql(context);
		}

		return res;
	}

}
