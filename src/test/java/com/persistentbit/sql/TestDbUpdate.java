package com.persistentbit.sql;

import com.persistentbit.sql.dbupdates.DbUpdater;
import com.persistentbit.sql.transactions.SQLTransactionRunner;
import org.junit.Test;


/**
 * User: petermuys
 * Date: 14/07/16
 * Time: 23:25
 */
public class TestDbUpdate extends TestWithTransactions{

    public static class TestUpdater extends DbUpdater{
        public TestUpdater(SQLTransactionRunner runner) {
            super(runner, "com.persistbit", "persist-sql.test", "/dbupdates/dbupdate_tests.sql");
        }

        public void withJavaUpdateTest(){
            log.info("java update executed");
        }
    }


    @Test
    public void testDbUpdate() {
        TestUpdater up = new TestUpdater(trans);
        up.update();
        log.info("SECOND UPDATE: ------------------------------");
        up.update();
    }
}
