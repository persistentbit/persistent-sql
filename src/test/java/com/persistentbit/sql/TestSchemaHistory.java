package com.persistentbit.sql;

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
        /* TODO check waarom dit niet meer werkt
        trans.trans(c -> {
            SchemaUpdateHistory uh = new SchemaUpdateHistoryImpl(trans);
            assert uh.isDone("com.persistbit.persist-sql","testupdatehistory") == false;
            uh.setDone("com.persistbit.persist-sql","testupdatehistory");
            assert uh.isDone("com.persistbit.persist-sql","testupdatehistory");
        });*/


    }

}
