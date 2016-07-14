package com.persistentbit.sql.connect;

import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Interface that should be used by sql client code to run sql code.<br>
 * @author Peter Muys
 * @since 14/07/2016
 */
public interface SQLRunner extends Supplier<Connection> {

    @FunctionalInterface
    interface SqlCodeWithResult<T>{
        T run(Connection c) throws SQLException;
    }
    @FunctionalInterface
    interface SqlCode{
        void run(Connection c) throws SQLException;
    }


    /**
     * Create a new connections.<br>
     * Should be closed by the client code<br>
     */
    @Override Connection get();

    /**
     * Run the provided code with the given connection.<br>
     * The given connection should not be closed by the client code<br>
     * @param code The code to run
     * @param <T> The value type returned
     * @return The T value from the code
     */
    default <T> T run(SqlCodeWithResult<T> code){
        try {
            try(Connection c = get()){
                return code.run(c);
            }
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }
    }
    /**
     * Run the provided code with the given connection.<br>
     * The given connection should not be closed by the client code<br>
     * @param code The code to run

     */
    default void run(SqlCode code){
        try {
            try(Connection c = get()){
                code.run(c);
            }
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }
    }

}
