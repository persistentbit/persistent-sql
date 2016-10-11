package com.persistentbit.sql;



import org.junit.Test;

import java.sql.PreparedStatement;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 19:36
 */
public class TestTransactions extends TestWithTransactions {

    @Test
    public void testTrans(){
        trans.trans(c -> {
            String sql = loader.getAll("create_test_table").head();
            log.fine(sql);
            PreparedStatement stat = c.prepareStatement(sql);
            stat.execute();
        });
    }

}
