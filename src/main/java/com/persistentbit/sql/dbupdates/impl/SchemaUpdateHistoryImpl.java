package com.persistentbit.sql.dbupdates.impl;

import com.persistentbit.core.logging.PLog;
import com.persistentbit.sql.dbupdates.SchemaUpdateHistory;
import com.persistentbit.sql.transactions.TransactionRunner;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Implements A {@link SchemaUpdateHistory} interface by using a db table<br>
 */
public class SchemaUpdateHistoryImpl implements SchemaUpdateHistory{
    static private PLog log = PLog.get(SchemaUpdateHistoryImpl.class);
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

    @Override
    public boolean isDone(String packageName, String updateName) {
        throw new RuntimeException("SchemaUpdateHistoryImpl.isDone TODO: Not yet implemented");
    }

    @Override
    public void setDone(String packageName, String updateName) {
        throw new RuntimeException("SchemaUpdateHistoryImpl.setDone TODO: Not yet implemented");
    }
    private void createTableIfNotExist() {
        EStat stat =stat();
        if(stat.tableExists(tableName) == false){
            log.info("Creating table " + tableName);
            stat.sql(
                    "CREATE TABLE " + tableName + " (",
                    "  createdDate TIMESTAMP          NOT NULL DEFAULT current_timestamp,",
                    "  package_name  VARCHAR(80)        NOT NULL,",
                    "  update_name  VARCHAR(80)        NOT NULL,",
                    "  CONSTRAINT " + tableName +"_uc UNIQUE (package_name,update_name)",
                    ")");
            stat.execute();

        }

    }

    @Override
    public boolean isDone(String packageName, String updateName) {
        runner.trans(c -> {
            try(PreparedStatement stat = c.prepareStatement("select count(10) from " + tableName +
                    "where package_name=?  and update_name=?")){
                stat.setString(1,packageName);
                stat.setString(2,updateName);
            }
        });
        EStat stat = stat();
        return stat.sql()
                .arg("projectName",projectName, "moduleName",moduleName,"updateName",updateName)
                .select().isEmpty() == false;
    }

    @Override
    public void setDone(String projectName, String moduleName, String updateName) {
        EStat stat = stat();
        stat.sql("insert into " + tableName,
                "(project_name,module_name,update_name) values(:projectName,:moduleName,:updateName)")
                .arg("projectName",projectName, "moduleName",moduleName,"updateName",updateName);
        stat.updateOne();
    }

    public boolean tableExists(String tableName){
        return runner.trans(c -> {
            DatabaseMetaData dbm = c.getMetaData();
            try(ResultSet rs = dbm.getTables(null,null,null,null)){
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

