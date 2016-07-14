package com.persistentbit.sql.transactions;



import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.ConnectionWrapper;
import com.persistentbit.sql.connect.SQLRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A SQLRunner for running code in transactions.<br>
 * @author Peter Muys
 * @since 29/02/2016
 */
public class SQLTransactionRunner implements SQLRunner {
    static private final Logger log = Logger.getLogger(SQLTransactionRunner.class.getName());

    private final Supplier<Connection>   connectionSupplier;

    private ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public SQLTransactionRunner(Supplier<Connection> connectionSupplier){
        this.connectionSupplier = connectionSupplier;
    }

    public SQLTransactionRunner(DataSource ds){
        this(() -> {
            try {
                return ds.getConnection();
            } catch (SQLException e) {
                log.severe(e.getMessage());
                throw new TransactionsException("Error while getting a new Database connection",e);
            }
        });
    }


    @Override
    public <T> T run(SqlCodeWithResult<T> code) {
        return doRun(() -> {
            try {
                return code.run(currentConnection.get());
            }catch(SQLException e){
                throw new PersistSqlException(e);
            }
        });
    }

    @Override
    public void run(SqlCode code){

        doRun(() -> {
            try {
                code.run(currentConnection.get());
            }catch(SQLException e){
                throw new PersistSqlException(e);
            }
            return null;
        });
    }


    @Override
    public Connection get() {
        return connectionSupplier.get();
    }


    public void runNew(SqlCode code){
        Connection prev = currentConnection.get();
        try {
            currentConnection.remove();
            run(code);
        }finally{
            currentConnection.set(prev);
        }
    }

    public <R> R runNew(SqlCodeWithResult<R> code){
        Connection prev = currentConnection.get();
        try {
            currentConnection.remove();
            return run(code);
        }finally{
            currentConnection.set(prev);
        }
    }





    private <R> R doRun(Callable<R> code){
        boolean isNewConnection = false;
        if(currentConnection.get() == null){
            currentConnection.set(connectionSupplier.get());
            try {
                currentConnection.get().setAutoCommit(false);
                isNewConnection = true;
            }catch (SQLException sql){
                throw new TransactionsException("Error while creating a new jdbc connection",sql);
            }
        }
        Connection con = currentConnection.get();
        try{
            R result = code.call();
            con.commit();
            return result;
        }catch(Exception e){
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            currentConnection.remove();
            throw new RuntimeException("Rolledback",e);
        }
        finally {
            if(isNewConnection){
                try{ con.close(); } catch(Exception e){ e.printStackTrace(); }
                currentConnection.remove();
            }
        }
    }
}
