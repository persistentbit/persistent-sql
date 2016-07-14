package com.persistentbit.sql;

import com.persistentbit.sql.dbupdates.SchemaUpdateHistory;
import com.persistentbit.sql.dbupdates.SchemaUpdateHistoryImpl;
import org.testng.annotations.Test;

/**
 * User: petermuys
 * Date: 14/07/16
 * Time: 22:36
 */
public class TestSchemaHistory extends TestWithTransactions {

    @Test
    void test(){
        SchemaUpdateHistory uh = new SchemaUpdateHistoryImpl(trans);
        assert uh.isDone("com.persistbit","persist-sql","testupdatehistory") == false;
        uh.setDone("com.persistbit","persist-sql","testupdatehistory");
        assert uh.isDone("com.persistbit","persist-sql","testupdatehistory");

    }
}
