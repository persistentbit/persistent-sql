package com.persistentbit.sql.dbbuilder;

import com.persistentbit.core.OK;
import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.dbbuilder.impl.DbBuilderImpl;
import com.persistentbit.sql.staticsql.DbWork;

/**
 * Service interface to keep track of database schema updates.<br>
 *
 * @see DbBuilderImpl
 * @author Peter Muys
 * @since 14/07/16
 */
public interface SchemaUpdateHistory{

	/**
	 * Is the database update for the provided version done ?
	 *
	 * @param packageName The package or module Name (ex. com.persistentbit.sql)
	 * @param updateName  The name of the update (ex. create_initial_tables)
	 *
	 * @return is the update done
	 */
	DbWork<Boolean> isDone(String packageName, String updateName);

	/**
	 * set the database update for the provided version as done.
	 *
	 * @param packageName The package or module Name (ex. com.persistentbit.sql)
	 * @param updateName  The name of the update (ex. create_initial_tables)
	 * @return executed ok?
	 */
	DbWork<OK> setDone(String packageName, String updateName);

	/**
	 * removes the update history for a given package.<br>
	 * @param packageName The packageName to remove the history for
	 * @return executed ok?
	 */
	DbWork<OK> removeUpdateHistory(String packageName);

	/**
	 * Get a list of all updates registered as done for a package.<br>
	 *
	 * @param packageName The package to get the list for
	 *
	 * @return List with all the update names for the given package
	 */
	DbWork<PList<String>> getUpdatesDone(String packageName);
}
