package com.persistentbit.sql.databases;

import com.persistentbit.sql.PersistSqlException;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public interface DbType{
    String getDatabaseName();
    String sqlWithLimit(long limit, String sql);
    String sqlWithLimitAndOffset(long limit, long offset, String sql);

    default String numberToString(String number, int charCount){
        return "CAST(" + number + " AS VARCHAR(" + + charCount + ")";
    }

    default String concatStrings(String s1, String s2){
        return "CONCAT(" + s1 + ", " + s2 + ")";
    }

    default String asLiteralString(String value){
        if(value == null){
            return null;
        }
        StringBuffer res = new StringBuffer();
        for(int t=0; t<value.length();t++){
            char c = value.charAt(t);
            if(c == '\'') {
                res.append("\'\'");
            } else if(c == '\"'){
                res.append("\"\"");
            } else {
                res.append(c);
            }
        }
        return "\'" + res.toString() + "\'";
    }

    default String asLiteralDate(LocalDate date){
        return "DATE '" +DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date) + "'";
    }

    default String asLiteralDateTime(LocalDateTime dateTime){
        return "TIMESTAMP '" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnnn").format(dateTime) + "'";
    }

    default String toUpperCase(String value){
        return "UCASE(" + value + ")";
    }

    default String toLowerCase(String value){
        return "LCASE(" + value + ")";
    }


    static void registerDriver(String driverClass){
        try {
            Driver driver = (Driver)Class.forName(driverClass).newInstance();
            DriverManager.registerDriver(driver);
        } catch (InstantiationException |IllegalAccessException|ClassNotFoundException|SQLException e) {
            throw new PersistSqlException(e);
        }
    }


}
