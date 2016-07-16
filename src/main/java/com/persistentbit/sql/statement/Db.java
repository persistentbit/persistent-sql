package com.persistentbit.sql.statement;

import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.dbdef.TableDefSupplierImpl;
import com.persistentbit.sql.objectmappers.ObjectRowMapper;
import com.persistentbit.sql.transactions.SQLTransactionRunner;

import java.sql.Connection;
import java.util.function.Supplier;

/**
 * User: petermuys
 * Date: 16/07/16
 * Time: 16:44
 */
public class Db {
    protected final SQLTransactionRunner  runner;
    protected final ObjectRowMapper   rowMapper;
    protected final TableDefSupplierImpl  tableDefSupplier;


    public Db(Supplier<Connection> connectionSupplier){
        this(new SQLTransactionRunner(connectionSupplier));
    }

    public Db(SQLTransactionRunner runner){
        this(runner,new ObjectRowMapper());
    }

    public Db(SQLTransactionRunner runner,ObjectRowMapper rowMapper){
        this(runner, rowMapper,new TableDefSupplierImpl(runner));
    }

    public Db(SQLTransactionRunner runner, ObjectRowMapper rowMapper, TableDefSupplierImpl tableDefSupplier) {
        this.runner = runner;
        this.rowMapper = rowMapper;
        this.tableDefSupplier = tableDefSupplier;
    }

    public SQLTransactionRunner getRunner() {
        return runner;
    }

    public ObjectRowMapper getRowMapper() {
        return rowMapper;
    }

    public TableDefSupplierImpl getTableDefSupplier() {
        return tableDefSupplier;
    }

    public <T> ETableStats tableStats(Class<T> objectClass, String tableName){
        return new ETableStats(runner,tableName,objectClass,tableDefSupplier,rowMapper);
    }
}
