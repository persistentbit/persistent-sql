package com.persistentbit.sql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 19:02
 */
public class TestDb {
    private InMemConnectionProvider dbConnector;
    @Before
    public void setupConnection() {
        dbConnector = new InMemConnectionProvider();
    }
    @After
    public void closeConnection() {
        dbConnector.close();
    }

    @Test
    public void testSingleConnection() throws Exception{
        Connection c = dbConnector.get();
        c.close();
    }
    @Test
    public void testMultipleConnection() throws Exception{
        Connection c1 = dbConnector.get();
        Connection c2 = dbConnector.get();
        c1.close();
        c2.close();
    }
}
