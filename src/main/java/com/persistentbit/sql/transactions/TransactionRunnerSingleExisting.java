package com.persistentbit.sql.transactions;

import com.persistentbit.core.logging.PLog;
import com.persistentbit.core.utils.NotYet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * TODO: Add comment
 *
 * @author Peter Muys
 * @since 11/10/2016
 */
public class TransactionRunnerSingleExisting implements TransactionRunner{
    static private PLog log = PLog.get(TransactionRunnerSingleExisting.class);
    private final Deque<Connection> connectionStack = new ArrayDeque<Connection>();
    private Supplier<Connection>    connectionSupplier;

    public TransactionRunnerSingleExisting(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }




    @Override
    public <T> T trans(SqlCodeWithResult<T> code) {
        Connection c;
        boolean isNew = false;
        synchronized (connectionStack){
            c = connectionStack.peek();
            if(c == null){
                c =connectionSupplier.get();
                connectionStack.push(c);
                isNew = true;
            }
        }
        try{
            T result = code.run(c);
            c.commit();
            return result;
        }catch(Exception e){
            try {
                c.rollback();
            } catch (SQLException e1) {
                log.error("Error while performing rollback",e1);
            }
            throw new RuntimeException("Rolledback",e);
        }
        finally {
            try{ c.close(); } catch(Exception e){log.error("Error while closing the db connection",e); }
            if(isNew){
                synchronized (connectionStack) { connectionStack.pop(); }
            }
        }
    }

    @Override
    public void trans(SqlCode code) {
        trans((c) -> { code.run(c); return null; });
    }

    @Override
    public <T> T transNew(SqlCodeWithResult<T> code) {

        if(true){ throw new NotYet("transNew Not yet supported on " + getClass().getName()); }
        Connection c;

        synchronized (connectionStack){
            c =connectionSupplier.get();
            connectionStack.push(c);
            try{
                T result = code.run(c);
                c.commit();
                return result;
            }catch(Exception e){
                try {
                    c.rollback();
                } catch (SQLException e1) {
                    log.error("Error while performing rollback",e1);
                }
                throw new RuntimeException("Rolledback",e);
            }
            finally {
                try{ c.close(); } catch(Exception e){log.error("Error while closing the db connection",e); }
                synchronized (connectionStack) { connectionStack.pop(); }
            }
        }

    }

    @Override
    public void transNew(SqlCode code) {
        transNew((c) -> { code.run(c); return null; });
    }
}
