package com.persistentbit.sql.connect;

import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class PooledConnectionProvider implements Supplier<Connection>{
    static private final Logger log = Logger.getLogger(PooledConnectionProvider.class.getName());

    private final Supplier<Connection> supplier;
    private final Consumer<Connection> resetter;
    private final int poolSize;
    private int activeConnections;
    private Object w = new Object();
    private final BlockingQueue<Connection> freeConnections;


    public PooledConnectionProvider(Supplier<Connection> supplier,int poolSize,  Consumer<Connection> connectionResetter) {
        this.supplier = supplier;
        this.poolSize = poolSize;
        this.resetter = connectionResetter;
        this.freeConnections = new LinkedBlockingQueue(poolSize);
    }
    public PooledConnectionProvider(Supplier<Connection> supplier,int poolSize){
        this(supplier,poolSize,(c)-> {});
    }

    @Override
    public synchronized Connection get() {
        if(freeConnections.isEmpty()){
            if(activeConnections<poolSize){
                //Nog geen pool opgebouwd...
                ConnectionWrapper con = newConnection(supplier.get());
                activeConnections++;
            }
        }
        Connection con;
        try {
            con = freeConnections.poll();
            if(con.isValid(0) == false){
                con = newConnection(supplier.get());
            }
            return con;
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }

    }

    private ConnectionWrapper newConnection(Connection realConnection){
        resetter.accept(realConnection);
        ConnectionWrapper con = new ConnectionWrapper(realConnection, new ConnectionWrapper.ConnectionHandler() {
            private boolean isCommit = false;
            @Override
            public void onClose(Connection connection) throws SQLException {
                if(isCommit == false || connection.getAutoCommit()){
                    connection.rollback();
                }
                freeConnections.add(connection);
                w.notify();
            }

            @Override
            public void onCommit(Connection connection) throws SQLException {
                isCommit = true;
                connection.commit();
            }

            @Override
            public void onRollback(Connection connection) throws SQLException {
                isCommit = true;
                connection.rollback();
            }

            @Override
            public void onAbort(Connection connection, Executor executor) throws SQLException {
                activeConnections--;
                connection.abort(executor);
            }
        });
        return con;
    }

    public synchronized void close(){
        while(activeConnections>0){
            Connection con = null;
            try {
                con = freeConnections.poll(1000, TimeUnit.MILLISECONDS);
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new PersistSqlException(e);
                }
                activeConnections--;
            } catch (InterruptedException e) {
                log.info("Waiting for connections to close");
            }
        }
    }
}
