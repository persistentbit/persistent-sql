package com.persistentbit.sql.dbupdates;

/**
 * Service interface to keep track of database schema updates.<br>
 * @see DbUpdater
 * User: petermuys
 * Date: 14/07/16
 * Time: 21:28
 */
public interface SchemaUpdateHistory {
    /**
     * Is the database update for the provided version done ?
     * @param packageName   The package or module Name (ex. com.persistentbit.sql)
     * @param updateName    The name of the update (ex. create_initial_tables)
     * @return is the update done
     */
    boolean isDone(String packageName,String updateName);
    /**
     * set the database update for the provided version as done.
     * @param packageName   The package or module Name (ex. com.persistentbit.sql)
     * @param updateName    The name of the update (ex. create_initial_tables)
     */
    void setDone(String packageName, String updateName);
}
