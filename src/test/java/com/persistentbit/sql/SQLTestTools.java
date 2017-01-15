package com.persistentbit.sql;

import com.persistentbit.core.logging.printing.LogPrinter;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.connect.SimpleConnectionSupplier;
import com.persistentbit.sql.databases.DbDerby;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.dbbuilder.DbBuilder;
import com.persistentbit.sql.dbbuilder.impl.DbBuilderImpl;
import com.persistentbit.sql.staticsql.DbContext;
import com.persistentbit.sql.staticsql.DbWork;
import com.persistentbit.sql.staticsql.DbWorkRunner;

import java.sql.Connection;
import java.util.function.Supplier;

/**
 * TODOC
 *
 * @author petermuys
 * @since 14/01/17
 */
public class SQLTestTools{
	static final LogPrinter	lp = ModuleSql.createLogPrinter(true);
	static final DbType               testDbType      = new DbDerby();
	static final String               testSchema      = null;
	static final Supplier<Connection> testDbConnector = new SimpleConnectionSupplier(
		"org.apache.derby.jdbc.EmbeddedDriver",
		DbDerby.urlInMemory(testSchema)
	);

	static final DbContext testDbContext = DbContext.of(testDbType, testSchema);
	static final DbBuilder builder       = new DbBuilderImpl("sqlTests", "/db/db_update.sql");

	public static <T> Result<T> run(DbWork<T> work) {
		return DbWorkRunner.run(testDbConnector, testDbContext, work);
	}

}


