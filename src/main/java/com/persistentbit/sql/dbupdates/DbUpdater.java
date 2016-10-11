package com.persistentbit.sql.dbupdates;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.dbupdates.impl.SchemaUpdateHistoryImpl;
import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.TransactionRunner;
import com.persistentbit.sql.transactions.TransactionRunnerPerThread;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * User: petermuys
 * Date: 18/06/16
 * Time: 21:42
 */
public class DbUpdater{
    protected final Logger log = Logger.getLogger(getClass().getName());
    protected final TransactionRunner runner;
    protected final String packageName;
    protected final SqlLoader sqlLoader;
    protected final SchemaUpdateHistory updateHistory;


    public DbUpdater(TransactionRunner runner, String packageName, String sqlResourceName, SchemaUpdateHistory updateHistory){
        this.runner =runner;
        this.packageName = packageName;
        this.sqlLoader = new SqlLoader(sqlResourceName);
        this.updateHistory = updateHistory;
    }
    public DbUpdater(TransactionRunnerPerThread runner, String packageName, String sqlResourceName){
        this(runner,packageName,sqlResourceName,new SchemaUpdateHistoryImpl(runner));
    }



    private String getFullName(String updateName) {
        return packageName + "." + updateName;
    }


    /**
     * Execute all the database update methods not registered in the SchemaHistory table.<br>
     * If there is a declared method in this class with the same name,
     * then that method is executed with a {@link java.sql.Connection} as argument.<br>
     */
    public void update() {

        Class<?> cls = this.getClass();

        PMap<String,Method> declaredMethods = PMap.empty();

        for(Method m :  cls.getDeclaredMethods()){
            declaredMethods = declaredMethods.put(m.getName(),m);
        }
        PMap<String,Method> methods = declaredMethods;
        sqlLoader.getAllSnippetNames().forEach( name -> {
            runner.trans((c) -> {
                if(updateHistory.isDone(packageName,name)){
                    return;
                }
                log.info("DBUpdate for  " + getFullName(name));
                methods.getOpt(name).ifPresent(m -> {
                    try {
                        m.invoke(this,c);
                    } catch (IllegalAccessException |InvocationTargetException e) {
                        throw new PersistSqlException(e);
                    }
                });
                sqlLoader.getAll(name).forEach(sql -> {
                    try{
                        try(Statement stat = c.createStatement()){
                            stat.execute(sql);
                        }
                    }catch(SQLException e){
                        throw new RuntimeException("Error executing " + getFullName(name),e);
                    }


                });
                updateHistory.setDone(packageName,name);
            });
        });

    }
}
