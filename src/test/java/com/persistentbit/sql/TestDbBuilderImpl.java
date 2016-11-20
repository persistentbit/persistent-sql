package com.persistentbit.sql;

import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.dbbuilder.impl.DbBuilderImpl;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.sql.Connection;

/**
 * Junit test DbUpdater
 *
 * @author petermuys
 * @since 31/10/16
 */
public class TestDbBuilderImpl extends DbBuilderImpl{

	public boolean javaUpdaterCalled = false;

	public TestDbBuilderImpl(DbType type,  String schema,TransactionRunner runner) {
		super(type,schema,runner, "com.persistentbit.sql.tests","/db/db_update.sql");
	}

	public void withJavaUpdateTest(Connection c) {
		javaUpdaterCalled = true;

	}
}