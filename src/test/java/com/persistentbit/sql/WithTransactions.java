package com.persistentbit.sql;

import com.persistentbit.sql.statement.SqlLoader;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.logging.Logger;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 19:37
 */
public class WithTransactions {
    protected Logger log = Logger.getLogger(this.getClass().getName());
    private InMemConnectionProvider dbConnector;
    protected Transactions trans;
    protected SqlLoader loader = new SqlLoader("Tests.sql");
    @BeforeTest
    public void setupTransactions() {
        dbConnector = new InMemConnectionProvider();
        trans = new Transactions(dbConnector);
    }
    @AfterTest
    public void closeTransactions() {
        trans = null;
        dbConnector.close();
    }
}
