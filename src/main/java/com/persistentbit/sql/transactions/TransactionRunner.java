package com.persistentbit.sql.transactions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Interface for working with db transactions.<br>
 * Makes it easy to run code in transactions.<br>
 *
 * @author Peter Muys
 * @since 14/07/2016
 */
public interface TransactionRunner{

	/**
	 * Run the provided code in an existing transaction. If there is no existing one, create a new transaction.<br>
	 *
	 * @param code The code to run
	 * @param <T>  The return value type
	 *
	 * @return The return value from the code.
	 */
	<T> T trans(SqlCodeWithResult<T> code);

	/**
	 * Run the provided code in an existing transaction. If there is no existing one, create a new transaction.<br>
	 *
	 * @param code The code to run
	 */
	void trans(SqlCode code);

	/**
	 * Run the provided code in a new transaction.<br>
	 *
	 * @param code The code to run
	 * @param <T>  The return value type
	 *
	 * @return The return value from the code.
	 */
	<T> T transNew(SqlCodeWithResult<T> code);

	/**
	 * Run the provided code in a new transaction.<br>
	 *
	 * @param code The code to run
	 */
	void transNew(SqlCode code);

	/**
	 * Get the underlying connection supplier
	 *
	 * @return the Sql connection supplier
	 */
	Supplier<Connection> getConnectionSupplier();

	@FunctionalInterface
	interface SqlCodeWithResult<T>{

		T run(Connection c) throws SQLException;
	}

	@FunctionalInterface
	interface SqlCode{

		void run(Connection c) throws SQLException;
	}


}
