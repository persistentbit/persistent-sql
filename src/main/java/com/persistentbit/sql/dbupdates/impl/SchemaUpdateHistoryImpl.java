package com.persistentbit.sql.dbupdates.impl;

import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.dbupdates.SchemaUpdateHistory;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Implements A {@link SchemaUpdateHistory} interface by using a db table<br>
 */
public class SchemaUpdateHistoryImpl implements SchemaUpdateHistory{

    private final TransactionRunner runner;
    private final String tableName;


    /**
     *
     * @param runner    The SQL runner for the db updates
     * @param tableName The table name for the schema history table
     */
    public SchemaUpdateHistoryImpl(TransactionRunner runner, String tableName){
        this.runner = runner;
        this.tableName = tableName;

        createTableIfNotExist();
    }

    /**
     * Creates an instance with 'schema_history' as table name
     * @param runner The SQL runner to use
     */
    public SchemaUpdateHistoryImpl(TransactionRunner runner){
        this(runner,"schema_history");
    }


    private void createTableIfNotExist() {
        runner.trans(c ->{
            if(tableExists(tableName)){
                return;
            }
            try(Statement stat = c.createStatement() ){
                stat.execute( "CREATE TABLE " + tableName + " (" +
                        "  createdDate TIMESTAMP          NOT NULL DEFAULT current_timestamp,"+
                        "  package_name  VARCHAR(80)        NOT NULL,"+
                        "  update_name  VARCHAR(80)        NOT NULL,"+
                        "  CONSTRAINT " + tableName +"_uc UNIQUE (package_name,update_name)"+
                        ")");

            }
        });


    }

    @Override
    public boolean isDone(String packageName, String updateName) {
        int count = runner.trans(c -> {
            try(PreparedStatement stat = c.prepareStatement("select count(1) from " + tableName +
                    " where package_name=?  and update_name=?")){
                stat.setString(1,packageName);
                stat.setString(2,updateName);
                try(ResultSet rs = stat.executeQuery()){
                    rs.next();
                    return rs.getInt(1);
                }
            }
        });
        return count > 0;
    }

    @Override
    public void setDone(String packageName, String updateName) {
        String sql = "insert into " + tableName +
                "(package_name,update_name) values(?,?)";
        runner.trans(c -> {
            try(PreparedStatement s = c.prepareStatement(sql)){
                s.setString(1,packageName);
                s.setString(2,updateName);
                if(s.executeUpdate() != 1){
                    throw new PersistSqlException("Expected 1 update for " + packageName + "." + updateName);
                }
            }
            return;
        });
    }

    public boolean tableExists(String tableName){
        return runner.trans(c -> {
            DatabaseMetaData dbm = c.getMetaData();

            try(ResultSet rs = dbm.getTables(null,null,
                    tableName,null)){
                while(rs.next()){
                    String tn = rs.getString("table_name");
                    if(tableName.equalsIgnoreCase(tableName)){
                        return true;
                    }
                }
                return false;
            }
        });

    }

}

