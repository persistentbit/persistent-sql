package com.persistentbit.sql.transactions;

import com.persistentbit.sql.connect.SQLRunner;

/**
 * Mixin interface for classes that runs jdbc sql code
 * User: petermuys
 * Date: 14/07/16
 * Time: 22:08
 */
public interface WithSQLRunner {
    /**
     * Get the SQl runner
     * @return The SQL runner
     */
    SQLRunner sqlRunner();

    default <T> T run(SQLRunner.SqlCodeWithResult<T> code) {
        return sqlRunner().run(code);
    }

    default void run(SQLRunner.SqlCode code){
        sqlRunner().run(code);
    }

}
