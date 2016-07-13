package com.persistentbit.sql.connect;

import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class SimpleConnectionProvider implements Supplier<Connection>{
    private final String url;
    private final String userName;
    private final String passWord;
    public SimpleConnectionProvider(String driverClass, String url, String userName, String password){
        try {
            Driver driver = (Driver)Class.forName(driverClass).newInstance();
            DriverManager.registerDriver(driver);
        } catch (InstantiationException |IllegalAccessException|ClassNotFoundException|SQLException  e) {
            throw new PersistSqlException(e);
        }
        this.url = url;
        this.passWord = password;
        this.userName = userName;
    }


    @Override
    public Connection get() {
        try {
            return DriverManager.getConnection(url,userName,passWord);
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }
    }
}
