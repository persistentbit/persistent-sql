package com.persistentbit.sql.dbupdates;

import com.persistentbit.core.Pair;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.statement.EStat;
import com.persistentbit.sql.statement.SqlLoader;
import com.persistentbit.sql.transactions.SQLTransactionRunner;
import com.persistentbit.sql.transactions.WithTransactions;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * User: petermuys
 * Date: 18/06/16
 * Time: 21:42
 */
public class DbUpdater implements WithTransactions {
    protected final Logger log = Logger.getLogger(getClass().getName());
    protected final SQLTransactionRunner    runner;
    protected final String projectName;
    protected final String moduleName;
    protected final SqlLoader sqlLoader;
    protected final SchemaUpdateHistory updateHistory;


    public DbUpdater(SQLTransactionRunner runner,String projectName,String moduleName,String sqlResourceName,SchemaUpdateHistory updateHistory){
        this.runner =runner;
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.sqlLoader = new SqlLoader(sqlResourceName);
        this.updateHistory = updateHistory;
    }
    public DbUpdater(SQLTransactionRunner runner,String projectName,String moduleName,String sqlResourceName){
        this(runner,projectName,moduleName,sqlResourceName,new SchemaUpdateHistoryImpl(runner));
    }

    @Override
    public SQLTransactionRunner sqlRunner() {
        return runner;
    }

    private String getFullName(String updateName) {
        return projectName+"." + moduleName + "." + updateName;
    }


    /**
     * Execute all the database update methods not registered in the SchemaHistory table
     */
    public void update() {

        Class<?> cls = this.getClass();
        Set<String> names = new HashSet<>(sqlLoader.getAllSnippetNames());
        List<Pair<String,Optional<Method>>> all = sqlLoader.getAllSnippetNames().stream().map(n -> new Pair<String,Optional<Method>>(n, Optional.empty())).collect(Collectors.toList());

        for(Method m :  cls.getDeclaredMethods()){
            if(names.contains(m.getName())){
                all.add(new Pair<>(m.getName(), Optional.of(m)));
            }
        }
        all.sort((a,b) -> a.getLeft().compareTo(b.getLeft()));
        all.forEach( m -> {
            run((c) -> {
                if(updateHistory.isDone(projectName,moduleName,m.getLeft())){
                    return;
                }
                log.info("DBUpdate for  " + getFullName(m.getLeft()));
                Method met = m.getRight().orElse(null);
                if(met != null) {
                    try {
                        met.invoke(this);
                    } catch (IllegalAccessException |InvocationTargetException e) {
                        throw new PersistSqlException(e);
                    }
                } else {
                    sqlLoader.getAll(m.getLeft()).forEach(sql -> {
                        EStat stat = new EStat(runner);
                        stat.sql(sql);
                        stat.execute();
                    });
                }
                updateHistory.setDone(projectName,moduleName,m.getLeft());
            });
        });

    }
}
