package com.persistentbit.sql.dbbuilder.impl;

import com.persistentbit.core.OK;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.logging.Log;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.dbbuilder.SchemaUpdateHistory;
import com.persistentbit.sql.sqlwork.DbTransManager;
import com.persistentbit.sql.staticsql.DbContext;
import com.persistentbit.sql.staticsql.DbWork;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Implements A {@link SchemaUpdateHistory} interface by using a db table<br>
 */
public class SchemaUpdateHistoryImpl implements SchemaUpdateHistory{

	private final String            tableName;


	/**
	 * Creates an instance with 'schema_history' as table name and no schema name
	 */
	public SchemaUpdateHistoryImpl() {
		this("schema_history");
	}




	/**
	 * @param tableName The table name for the schema history table.
	 */
	public SchemaUpdateHistoryImpl(String tableName) {
		this.tableName = tableName;
	}


	@Override
	public DbWork<Boolean> isDone(String packageName, String updateName) {
		return DbWork.function(packageName, updateName).code(log -> (dbc, tm) ->
			createTableIfNotExist()
				.flatMap(ok -> {
					String sql = "select count(1) from " + dbc.getFullTableName(tableName) +
						" where package_name=?  and update_name=?";
					log.info("Executing " + sql);
					try(PreparedStatement stat = tm.get().prepareStatement(sql)) {
						stat.setString(1, packageName);
						stat.setString(2, updateName);
						try(ResultSet rs = stat.executeQuery()) {
							rs.next();
							return Result.success(rs.getInt(1));
						}
					}
				})
				.map(count -> count > 0)
				.execute(dbc, tm)
		);
	}

	private DbWork<OK> createTableIfNotExist() {
		return DbWork.function().code(log -> (dbc, tm) ->
			schemaHistoryTableExists()
				.flatMap(exists -> {
					if(exists == false) {
						try(Statement stat = tm.get().createStatement()) {
							stat.execute("CREATE TABLE " + dbc.getFullTableName(tableName) + " (" +
											 "  createdDate TIMESTAMP          NOT NULL DEFAULT current_timestamp," +
											 "  package_name  VARCHAR(80)        NOT NULL," +
											 "  update_name  VARCHAR(80)        NOT NULL," +
											 "  CONSTRAINT " + tableName + "_uc UNIQUE (package_name,update_name)" +
											 ")");
						}
						return OK.result;
					}
					return OK.result;
				})
				.execute(dbc, tm)
		);
	}

	private DbWork<Boolean> schemaHistoryTableExists() {
		return DbWork.function().code(l -> (dbc, tm) ->
													   Result.success(
														   tableExists(dbc, tm, tableName) ||
															   tableExists(dbc, tm, tableName.toLowerCase()) ||
															   tableExists(dbc, tm, tableName.toUpperCase())
													   )
		);
	}

	private boolean tableExists(DbContext dbc, DbTransManager tm, String tableName) {
		return Log.function(tableName).code(l -> {
			DatabaseMetaData dbm = tm.get().getMetaData();

			try(ResultSet rs = dbm.getTables(
				null,
				dbc.getSchemaName().orElse(null),
				tableName, null
			)) {
				while(rs.next()) {
					String tn = rs.getString("table_name");
					if(tableName.equalsIgnoreCase(tableName)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public DbWork<PList<String>> getUpdatesDone(String packageName) {
		return (dbc, tm) -> Result.function(packageName).code(l ->

																  schemaHistoryTableExists()
																	  .flatMap(exists -> {
																		  PList<String> result = PList.empty();
																		  if(exists) {
																			  try(PreparedStatement stat = tm.get()
																				  .prepareStatement("select update_name from " + dbc
																					  .getFullTableName(tableName) +
																										" where package_name=?")) {
																				  stat.setString(1, packageName);

																				  try(ResultSet rs = stat
																					  .executeQuery()) {
																					  while(rs.next()) {
																						  result = result
																							  .plus(rs.getString(1));
																					  }

																				  }
																			  }
																		  }
																		  return Result.success(result);
																	  })
																	  .execute(dbc, tm)
		);

	}


	@Override
	public DbWork<OK> setDone(String packageName, String updateName) {
		DbWork<OK> insert = (dbc, tm) -> {
			String sql = "insert into " + dbc.getFullTableName(tableName) +
				"(package_name,update_name) values(?,?)";
			try(PreparedStatement s = tm.get().prepareStatement(sql)) {
				s.setString(1, packageName);
				s.setString(2, updateName);
				if(s.executeUpdate() != 1) {
					return Result
						.failure(new PersistSqlException("Expected 1 update for " + packageName + "." + updateName));
				}
			}
			return OK.result;
		};
		return (dbc, tm) -> Result.function(packageName, updateName).code(l ->
																			  createTableIfNotExist()
																				  .flatMap(ok -> insert
																					  .execute(dbc, tm))
																				  .execute(dbc, tm)
		);
	}

	@Override
	public DbWork<OK> removeUpdateHistory(String packageName) {
		DbWork<OK> delete = (dbc, tm) -> {
			String sql = "delete from " + dbc.getFullTableName(tableName) + " where package_name = ?";
			try(PreparedStatement s = tm.get().prepareStatement(sql)) {
				s.setString(1, packageName);
				s.executeUpdate();
			}
			try(PreparedStatement s = tm.get()
				.prepareStatement("select count(1) from " + dbc.getFullTableName(tableName))) {
				int count = 0;
				try(ResultSet rs = s.executeQuery()) {
					rs.next();
					count = rs.getInt(1);
				}
				if(count == 0) {
					try(PreparedStatement ds = tm.get()
						.prepareStatement("drop table " + dbc.getFullTableName(tableName))) {
						ds.executeUpdate();
					}
				}

			}
			return OK.result;
		};

		return (dbc, tm) -> Result.function(packageName).code(l ->
																  schemaHistoryTableExists()
																	  .flatMap(exists -> {
																		  if(exists) {
																			  return delete.execute(dbc, tm);
																		  }
																		  return OK.result;
																	  })
																	  .execute(dbc, tm)
		);




	}
}

