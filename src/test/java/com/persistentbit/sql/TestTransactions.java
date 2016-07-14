package com.persistentbit.sql;

import org.testng.annotations.Test;

import java.sql.PreparedStatement;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 19:36
 */
public class TestTransactions extends TestWithTransactions {

    @Test
    void testTrans(){
        trans.run(c -> {
            String sql = loader.getOne("create_test_table");
            log.fine(sql);
            PreparedStatement stat = c.prepareStatement(sql);
            stat.execute();
        });
    }

}
