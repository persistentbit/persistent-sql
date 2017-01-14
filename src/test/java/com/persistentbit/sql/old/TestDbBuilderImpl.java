package com.persistentbit.sql.old;

import com.persistentbit.sql.dbbuilder.impl.DbBuilderImpl;

import java.sql.Connection;

/**
 * Junit test DbUpdater
 *
 * @author petermuys
 * @since 31/10/16
 */
public class TestDbBuilderImpl extends DbBuilderImpl{

	public boolean javaUpdaterCalled = false;

	public TestDbBuilderImpl() {
		super("com.persistentbit.sql.tests", "/db/db_update.sql");
	}

	public void withJavaUpdateTest(Connection c) {
		javaUpdaterCalled = true;

	}
}
