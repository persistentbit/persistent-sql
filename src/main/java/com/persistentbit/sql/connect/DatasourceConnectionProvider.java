package com.persistentbit.sql.connect;

import com.persistentbit.sql.PersistSqlException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: petermuys
 * Date: 14/07/16
 * Time: 21:13
 */
public class DatasourceConnectionProvider implements SQLRunner{
    private final DataSource    dataSource;

    public DatasourceConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection get() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }
    }
}
