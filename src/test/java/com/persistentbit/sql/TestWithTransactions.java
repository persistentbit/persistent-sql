package com.persistentbit.sql;

import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.SQLTransactionRunner;
import org.testng.annotations.*;

import java.util.logging.Logger;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 19:37
 */
public class TestWithTransactions {
    protected Logger log = Logger.getLogger(this.getClass().getName());
    protected InMemConnectionProvider dbConnector;
    protected SQLTransactionRunner trans;
    protected SqlLoader loader;

    @BeforeClass(alwaysRun = true)
    public void setupTransactions() {
        dbConnector = new InMemConnectionProvider();
        trans = new SQLTransactionRunner(dbConnector);
        loader = new SqlLoader("Tests.sql");
    }
    @AfterClass(alwaysRun = true)
    public void closeTransactions() {
        trans = null;
        dbConnector.close();
        dbConnector = null;
    }
}
