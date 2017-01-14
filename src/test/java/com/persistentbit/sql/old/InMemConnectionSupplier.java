package com.persistentbit.sql.old;

import com.persistentbit.sql.connect.PooledConnectionSupplier;
import com.persistentbit.sql.connect.SimpleConnectionSupplier;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 18:55
 */
public class InMemConnectionSupplier extends PooledConnectionSupplier{


    public InMemConnectionSupplier() {
        super(new SimpleConnectionSupplier(
               "org.apache.derby.jdbc.EmbeddedDriver",
                "jdbc:derby:memory:junittests;create=true"
        ),10);
    }


}
