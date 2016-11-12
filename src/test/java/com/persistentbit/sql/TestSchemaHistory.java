package com.persistentbit.sql;

import com.persistentbit.sql.dbbuilder.SchemaUpdateHistory;
import com.persistentbit.sql.dbbuilder.impl.SchemaUpdateHistoryImpl;
import org.junit.Test;


/**
 * User: petermuys
 * Date: 14/07/16
 * Time: 22:36
 */
public class TestSchemaHistory extends AbstractTestWithTransactions{

    @Test
    public void testa(){
        assert trans != null;

        trans.trans(c -> {
            String              packageName = "com.persistbit.persist-sql";
            SchemaUpdateHistory uh          = new SchemaUpdateHistoryImpl(trans);
            assert uh.isDone(packageName, "testupdatehistory") == false;
            uh.setDone(packageName, "testupdatehistory");
            assert uh.isDone(packageName, "testupdatehistory");
            uh.removeUpdateHistory(packageName + "_not");
            assert uh.isDone(packageName, "testupdatehistory");
            uh.removeUpdateHistory(packageName);
            assert uh.isDone(packageName, "testupdatehistory") == false;
        });


    }

}
