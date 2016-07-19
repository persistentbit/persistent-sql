package com.persistentbit.sql.databases;

import com.persistentbit.sql.PersistSqlException;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public interface DbType{
    String getDatabaseName();
    String sqlWithLimit(long limit, String sql);
    String sqlWithLimitAndOffset(long limit, long offset, String sql);


    static public void registerDriver(String driverClass){
        try {
            Driver driver = (Driver)Class.forName(driverClass).newInstance();
            DriverManager.registerDriver(driver);
        } catch (InstantiationException |IllegalAccessException|ClassNotFoundException|SQLException e) {
            throw new PersistSqlException(e);
        }
    }
}
