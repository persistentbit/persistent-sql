package com.persistentbit.sql.dbbuilder.impl;

import com.persistentbit.core.Nothing;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.exceptions.RtSqlException;
import com.persistentbit.core.logging.Log;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.dbbuilder.DbBuilder;
import com.persistentbit.sql.dbbuilder.SchemaUpdateHistory;
import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A {@link DbBuilder} implementation that uses resource files to create/update/drop a database schema.<br>
 * If a snippet exists with the name DropAll, then that snippet will be used in the {@link #dropAll()} method.<br>
 * if a snippet exists with the name OnceBefore, then tat snippit will be run once before
 * every call to {@link #buildOrUpdate()} and {@link #dropAll()}.<br>
 *
 * @author Peter Muys
 * @see SchemaUpdateHistory
 * @since 18/06/16
 */
public class DbBuilderImpl implements DbBuilder{

	public static final String onceBeforeSnippetName = "OnceBefore";
	public static final String dropAllSnippetName    = "DropAll";


	protected final DbType dbType;
	protected final String schema;
	protected final TransactionRunner   runner;
	protected final String              packageName;
	protected final SqlLoader           sqlLoader;
	protected final SchemaUpdateHistory updateHistory;


	/**
	 * Create a new {@link DbBuilder} with the given parameters and a default
	 * {@link SchemaUpdateHistoryImpl} to keep track of the updates already done.
	 *
	 * @param dbType          The Database Type
	 * @param schema          The Optional schema name.
	 * @param runner          The Transaction runner to use
	 * @param packageName     The packageName, used to keep track of the updates already done.
	 * @param sqlResourceName The name of the java resource file containing the sql statements
	 */
	public DbBuilderImpl(DbType dbType,String schema,TransactionRunner runner, String packageName, String sqlResourceName) {
		this(dbType, schema, runner, packageName, sqlResourceName, new SchemaUpdateHistoryImpl(runner, schema));
	}

	/**
	 *
	 * @param dbType The Database Type
	 * @param schema The Optional schema name.
	 * @param runner The Transaction runner to use
	 * @param packageName The packageName, used to keep track of the updates already done.
	 * @param sqlResourceName The name of the java resource file containing the sql statements
	 * @param updateHistory The {@link SchemaUpdateHistory} to use
	 */
	public DbBuilderImpl(DbType dbType, String schema, TransactionRunner runner,
						 String packageName, String sqlResourceName,
						 SchemaUpdateHistory updateHistory
	) {

		this.dbType = dbType;
		this.schema = schema;
		this.runner = runner;
		this.packageName = packageName;
		this.sqlLoader = new SqlLoader(sqlResourceName);
		this.updateHistory = updateHistory;
	}

	@Override
	public void buildOrUpdate() {
		Log.function().code(log -> {
			runOnceBefore();
			//First, find all declared method on this class
			Class<?> cls = this.getClass();

			PMap<String, Method> declaredMethods = PMap.empty();

			for(Method m : cls.getDeclaredMethods()) {
				declaredMethods = declaredMethods.put(m.getName().toLowerCase(), m);
			}
			PMap<String, Method> methods = declaredMethods;

			//Loop over all snippets and execute
			sqlLoader.getAllSnippetNames().forEach(name -> runner.trans((c) -> {
				//Skip Drop all and once before snippet
				if(name.equalsIgnoreCase(dropAllSnippetName) || name.equalsIgnoreCase(onceBeforeSnippetName)) {
					return;
				}
				//Is Snippet already executed ?
				if(updateHistory.isDone(packageName, name)) {
					return;
				}
				log.info("DBUpdate for  " + getFullName(name));

				//Set the default schema if it is defined.
				setDefaultSchema(c);

				//If a method with this name exists -> execute it
				methods.getOpt(name).ifPresent(m -> {
					try {
						m.invoke(this, c);
					} catch(IllegalAccessException | InvocationTargetException e) {
						throw new PersistSqlException(e);
					}
				});
				sqlLoader.getAll(name).forEach(sql -> executeSql(c, name, sql));
				updateHistory.setDone(packageName, name);
			}));
			return Nothing.inst;
		});

	}

	private String getFullName(String updateName) {
		return packageName + "." + updateName;
	}

	/**
	 * Execute an sql statement.
	 *
	 * @param c    The connection to use
	 * @param name The name of the snippet for error reporting
	 * @param sql  The sql statement
	 */
	private void executeSql(Connection c, String name, String sql) {
		try {
			try(Statement stat = c.createStatement()) {
				stat.execute(sql);
			}
		} catch(SQLException e) {
			throw new PersistSqlException("Error executing " + getFullName(name) + ": " + sql, e);
		}
	}

	/**
	 * Run the snippet with the name OnceBefore ({@link #onceBeforeSnippetName})
	 */
	private void runOnceBefore() {
		Log.function().code(log -> {
			if(sqlLoader.hasSnippet(onceBeforeSnippetName) == false) {
				return Nothing.inst; //we are done here
			}
			PList<String> sqlList = sqlLoader.getAll(onceBeforeSnippetName);
			sqlList.forEach(sql ->
								runner.trans(c -> {
									try {
										executeSql(c, onceBeforeSnippetName, sql);
									} catch(Exception e) {
										log.error("Error executing sql '" + sql + "': " + e.getMessage());
									}
								})
			);
			return Nothing.inst;
		});

	}

	/**
	 * Executes the snippet 'DropAll' and removes
	 * the update history for this package.<br>
	 *
	 * @return true if dropAll executed without errors
	 */
	@Override
	public boolean dropAll() {
		return Log.function().code(log -> {
			if(sqlLoader.hasSnippet(dropAllSnippetName) == false) {
				throw new PersistSqlException("Can't find SQL code 'DropAll' in " + sqlLoader);
			}
			runOnceBefore();
			PList<String> sqlList = sqlLoader.getAll(dropAllSnippetName);
			boolean allOk = sqlList.map(sql ->
											runner.trans(c -> {
												setDefaultSchema(c);
												try {
													executeSql(c, dropAllSnippetName, sql);
													return true;
												} catch(Exception e) {
													log.error("Error executing sql '" + sql + "': " + e.getMessage());
													return false;
												}
											})
			).find(ok -> ok == false).orElse(true);
			updateHistory.removeUpdateHistory(packageName);
			return allOk;
		});

	}

	/**
	 * If a schema name is defined in the constructor,
	 * then this function will set the default schema for the connection.<br>
	 *
	 * @param c The connection to set.
	 */
	private void setDefaultSchema(Connection c) {
		if(schema != null) {
			RtSqlException.tryRun(() -> {
				try(Statement stat = c.createStatement()) {
					stat.execute(dbType.setCurrentSchemaStatement(schema));
				}
			});
		}
	}

	@Override
	public boolean hasUpdatesThatAreDone() {
		return updateHistory.getUpdatesDone(packageName).isEmpty() == false;
	}
}
