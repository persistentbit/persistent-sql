package com.persistentbit.sql;



import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author Peter Muys
 * @since 29/02/2016
 */
public class Transactions implements Supplier<Connection> {
    static private final Logger log = Logger.getLogger(Transactions.class.getName());

    private final Supplier<Connection>   connectionSupplier;

    private ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public Transactions(Supplier<Connection> connectionSupplier){
        this.connectionSupplier = connectionSupplier;
    }

    public Transactions(DataSource ds){
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
    public Connection get() {
        Connection c = currentConnection.get();
        if(c == null){
            throw new TransactionsException("Not running in a transaction!");
        }
        return c;
    }


    public void runNew(Runnable code){
        runNew(() -> {
            code.run();
            return null;
        });
    }

    public <R> R runNew(Callable<R> code){
        Connection prev = currentConnection.get();
        try {
            currentConnection.remove();
            return run(code);
        }finally{
            currentConnection.set(prev);
        }
    }

    public void run(Runnable code){
        run(() -> {
            code.run();
            return null;
        });
    }

    public <R> R run(Callable<R> code){
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
