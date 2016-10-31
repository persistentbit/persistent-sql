package com.persistentbit.sql.dbupdates;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.dbupdates.impl.SchemaUpdateHistoryImpl;
import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used to create, update or drop all tables
 * in a database.<br>
 *
 * @author Peter Muys
 * @see SchemaUpdateHistory
 * @since 18/06/16
 */
public class DbUpdater{

	public static final String dropAllSnippetName = "DropAll";
	protected final PLog                log;
	protected final TransactionRunner   runner;
	protected final String              packageName;
	protected final SqlLoader           sqlLoader;
	protected final SchemaUpdateHistory updateHistory;

	public DbUpdater(TransactionRunner runner, String packageName, String sqlResourceName) {
		this(runner, packageName, sqlResourceName, new SchemaUpdateHistoryImpl(runner));
	}

	public DbUpdater(TransactionRunner runner, String packageName, String sqlResourceName,
					 SchemaUpdateHistory updateHistory
	) {
		this.log = PLog.get(getClass());
		this.runner = runner;
		this.packageName = packageName;
		this.sqlLoader = new SqlLoader(sqlResourceName);
		this.updateHistory = updateHistory;
	}

	/**
	 * Execute all the database update methods not registered in the SchemaHistory table.<br>
	 * If there is a declared method in this class with the same name,
	 * then that method is executed with a {@link Connection} as argument.<br>
	 */
	public void update() {
		//First, find all declared method on this class
		Class<?> cls = this.getClass();

		PMap<String, Method> declaredMethods = PMap.empty();

		for(Method m : cls.getDeclaredMethods()) {
			declaredMethods = declaredMethods.put(m.getName(), m);
		}
		PMap<String, Method> methods = declaredMethods;

		//Loop over all snippets and execute
		sqlLoader.getAllSnippetNames().forEach(name -> runner.trans((c) -> {
			//Skip Drop all snippet
			if(name.equalsIgnoreCase(dropAllSnippetName)) {
				return;
			}
			//Is Snippet already executed ?
			if(updateHistory.isDone(packageName, name)) {
				return;
			}
			log.info("DBUpdate for  " + getFullName(name));
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

	}

	private String getFullName(String updateName) {
		return packageName + "." + updateName;
	}

	private void executeSql(Connection c, String name, String sql) {
		try {
			try(Statement stat = c.createStatement()) {
				stat.execute(sql);
			}
		} catch(SQLException e) {
			throw new PersistSqlException("Error executing " + getFullName(name) + ": " + sql);
		}
	}

	/**
	 * Executes the snippet 'DropAll' and removes
	 * the update history for this package.<br>
	 *
	 * @return true if dropAll executed without errors
	 */
	public boolean dropAll() {

		if(sqlLoader.hasSnippet(dropAllSnippetName) == false) {
			throw new PersistSqlException("Can't find SQL code 'DropAll' in " + sqlLoader);
		}
		PList<String> sqlList = sqlLoader.getAll(dropAllSnippetName);
		boolean allOk = sqlList.map(sql ->
										runner.trans(c -> {
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
	}
}
