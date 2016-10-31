package com.persistentbit.sql;

import com.persistentbit.sql.dbupdates.DbUpdater;
import com.persistentbit.sql.transactions.TransactionRunner;

/**
 * Junit test DbUpdater
 *
 * @author petermuys
 * @since 31/10/16
 */
public class TestDbUpdater extends DbUpdater{

	public TestDbUpdater(TransactionRunner runner) {
		super(runner, "com.persistentbit.sql.tests","/db/db_update.sql");
	}
}
