package com.persistentbit.sql.dbbuilder;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Loads a Sql script using {@link SqlLoader} and run selected snippets.
 *
 * @author Peter Muys
 * @see SqlLoader
 */
public class DbScriptRunner{

	protected final Logger log = Logger.getLogger(getClass().getName());
	protected final TransactionRunner runner;
	protected final String            packageName;
	protected final SqlLoader         sqlLoader;


	public DbScriptRunner(TransactionRunner runner, String packageName, String sqlResourceName) {
		this.runner = runner;
		this.packageName = packageName;
		this.sqlLoader = new SqlLoader(sqlResourceName);

	}

	public void run(String name) {
		log.info("Executing " + getFullName(name));
		PList<String> snippets = sqlLoader.getAll(name);
		runner.trans((c) -> {

			sqlLoader.getAll(name).forEach(sql -> {
				try {
					try(Statement stat = c.createStatement()) {
						stat.execute(sql);
					}
				} catch(SQLException e) {
					throw new RuntimeException("Error executing " + getFullName(name), e);
				}


			});

		});

	}

	private String getFullName(String updateName) {
		return packageName + "." + updateName;
	}

}
