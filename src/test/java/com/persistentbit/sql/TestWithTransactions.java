package com.persistentbit.sql;

import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.TransactionRunnerPerThread;
import org.junit.After;
import org.junit.Before;

import java.util.logging.Logger;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 19:37
 */
public class TestWithTransactions {
    protected Logger log = Logger.getLogger(this.getClass().getName());
    protected InMemConnectionProvider dbConnector;
    protected TransactionRunnerPerThread trans;
    protected SqlLoader loader;

    @Before
    public void setupTransactions() {
        dbConnector = new InMemConnectionProvider();
        trans = new TransactionRunnerPerThread(dbConnector);
        loader = new SqlLoader("Tests.sql");
    }
    @After
    public void closeTransactions() {
        trans = null;
        dbConnector.close();
        dbConnector = null;
    }
}
