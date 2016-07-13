package com.persistentbit.sql;

import com.persistentbit.sql.connect.PooledConnectionProvider;
import com.persistentbit.sql.connect.SimpleConnectionProvider;

/**
 * User: petermuys
 * Date: 13/07/16
 * Time: 18:55
 */
public class InMemConnectionProvider extends PooledConnectionProvider{
    public InMemConnectionProvider(){
        super(new SimpleConnectionProvider(
               "org.apache.derby.jdbc.EmbeddedDriver",
                "jdbc:derby:memory:junittests;create=true"
        ),10);
    }


}
