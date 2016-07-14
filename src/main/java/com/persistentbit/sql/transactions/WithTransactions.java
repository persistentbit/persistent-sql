package com.persistentbit.sql.transactions;

import com.persistentbit.sql.connect.SQLRunner;

/**
 * Mixin interface for classes that use transactions
 * User: petermuys
 * Date: 14/07/16
 * Time: 21:17
 */
public interface WithTransactions extends WithSQLRunner{
    /**
     * Get the Transaction runner
     * @return The transactions runner
     */
    SQLTransactionRunner sqlRunner();

    default void runNew(SQLRunner.SqlCode code){
        sqlRunner().runNew(code);
    }

    default <R> R runNew(SQLRunner.SqlCodeWithResult<R> code){
        return sqlRunner().runNew(code);
    }

}
