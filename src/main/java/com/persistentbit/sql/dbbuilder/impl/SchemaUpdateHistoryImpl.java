package com.persistentbit.sql.dbbuilder.impl;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.dbbuilder.SchemaUpdateHistory;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Implements A {@link SchemaUpdateHistory} interface by using a db table<br>
 */
public class SchemaUpdateHistoryImpl implements SchemaUpdateHistory{

	static private PLog log = PLog.get(SchemaUpdateHistoryImpl.class);
	private final TransactionRunner runner;
	private final String            tableName;


	/**
	 * Creates an instance with 'schema_history' as table name
	 *
	 * @param runner The SQL runner to use
	 */
	public SchemaUpdateHistoryImpl(TransactionRunner runner) {
		this(runner, "schema_history");
	}

	/**
	 * @param runner    The SQL runner for the db updates
	 * @param tableName The table name for the schema history table.
	 */
	public SchemaUpdateHistoryImpl(TransactionRunner runner, String tableName) {
		this.runner = runner;
		this.tableName = tableName;


	}

	@Override
	public boolean isDone(String packageName, String updateName) {
		createTableIfNotExist();
		int count = runner.trans(c -> {
			try(PreparedStatement stat = c.prepareStatement("select count(1) from " + tableName +
																" where package_name=?  and update_name=?")) {
				stat.setString(1, packageName);
				stat.setString(2, updateName);
				try(ResultSet rs = stat.executeQuery()) {
					rs.next();
					return rs.getInt(1);
				}
			}
		});
		return count > 0;
	}

	private void createTableIfNotExist() {
		runner.trans(c -> {
			if(schemaHistoryTableExists()) {
				return;
			}
			try(Statement stat = c.createStatement()) {
				stat.execute("CREATE TABLE " + tableName + " (" +
								 "  createdDate TIMESTAMP          NOT NULL DEFAULT current_timestamp," +
								 "  package_name  VARCHAR(80)        NOT NULL," +
								 "  update_name  VARCHAR(80)        NOT NULL," +
								 "  CONSTRAINT " + tableName + "_uc UNIQUE (package_name,update_name)" +
								 ")");
				c.commit();
			}
		});


	}

	private boolean schemaHistoryTableExists() {
		return tableExists(tableName) || tableExists(tableName.toLowerCase()) || tableExists(tableName.toUpperCase());
	}

	private boolean tableExists(String tableName) {
		return runner.trans(c -> {


			DatabaseMetaData dbm = c.getMetaData();

			try(ResultSet rs = dbm.getTables(null, null,
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
	public PList<String> getUpdatesDone(String packageName) {
		return runner.trans(c -> {
			PList<String> result = PList.empty();
			if(schemaHistoryTableExists()) {
				try(PreparedStatement stat = c.prepareStatement("select update_name from " + tableName +
																	" where package_name=?")) {
					stat.setString(1, packageName);

					try(ResultSet rs = stat.executeQuery()) {
						while(rs.next()) {
							result = result.plus(rs.getString(1));
						}

					}
				}
			}
			return result;
		});
	}

	@Override
	public void setDone(String packageName, String updateName) {
		createTableIfNotExist();
		String sql = "insert into " + tableName +
			"(package_name,update_name) values(?,?)";
		runner.trans(c -> {
			try(PreparedStatement s = c.prepareStatement(sql)) {
				s.setString(1, packageName);
				s.setString(2, updateName);
				if(s.executeUpdate() != 1) {
					throw new PersistSqlException("Expected 1 update for " + packageName + "." + updateName);
				}
			}
			return;
		});
	}

	@Override
	public void removeUpdateHistory(String packageName) {
		runner.trans(c -> {
			String sql = "delete from " + tableName + " where package_name = ?";
			if(schemaHistoryTableExists()) {
				//delete all records for the package
				try(PreparedStatement s = c.prepareStatement(sql)) {
					s.setString(1, packageName);
					s.executeUpdate();
				}
				//check if there are other records left
				//and if not -> delete the history table
				try(PreparedStatement s = c.prepareStatement("select count(1) from " + tableName)) {
					int count = 0;
					try(ResultSet rs = s.executeQuery()) {
						rs.next();
						count = rs.getInt(1);
					}
					if(count == 0) {
						try(PreparedStatement ds = c.prepareStatement("drop table " + tableName)) {
							ds.executeUpdate();
						}
					}

				}

			}
		});

	}
}

