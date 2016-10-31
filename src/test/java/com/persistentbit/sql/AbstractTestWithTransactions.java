package com.persistentbit.sql;

import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.TransactionRunnerPerThread;
import org.junit.After;
import org.junit.Before;

import java.util.logging.Logger;

/**
 * @author Peter Muys
 * @since 13/07/16
 */
public abstract class AbstractTestWithTransactions{
    protected Logger log = Logger.getLogger(this.getClass().getName());
    protected InMemConnectionProvider dbConnector;
    protected TransactionRunnerPerThread trans;
    protected TestDbUpdater updater;
    protected SqlLoader loader;

    @Before
    public void setupTransactions() {
        dbConnector = new InMemConnectionProvider();
        trans = new TransactionRunnerPerThread(dbConnector);
        updater = new TestDbUpdater(trans);
        loader = new SqlLoader("/db/Tests.sql");
        updater.dropAll();
        updater.update();
    }
    @After
    public void closeTransactions() {
        updater.dropAll();
        trans = null;
        dbConnector.close();
        dbConnector = null;
    }
}
