package com.persistentbit.sql.dbupdates;

import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.statement.EStat;
import com.persistentbit.sql.transactions.WithSQLRunner;

import java.util.logging.Logger;

/**
 * Implements A {@link SchemaUpdateHistory} interface by using a db table<br>
 */
public class SchemaUpdateHistoryImpl implements SchemaUpdateHistory,WithSQLRunner{
    static private Logger log = Logger.getLogger(SchemaUpdateHistoryImpl.class.getName());
    private final SQLRunner runner;
    private final String tableName;


    /**
     *
     * @param runner    The SQL runner for the db updates
     * @param tableName The table name for the schema history table
     */
    public SchemaUpdateHistoryImpl(SQLRunner runner,String tableName){
        this.runner = runner;
        this.tableName = tableName;

        createTableIfNotExist();
    }

    /**
     * Creates an instance with 'schema_history' as table name
     * @param runner The SQL runner to use
     */
    public SchemaUpdateHistoryImpl(SQLRunner runner){
        this(runner,"schema_history");
    }

    @Override
    public SQLRunner sqlRunner() {
        return runner;
    }

    private EStat stat() {
        return new EStat(runner);
    }


    private void createTableIfNotExist() {
        EStat stat =stat();
        if(stat.tableExists(tableName) == false){
            log.info("Creating table " + tableName);
            stat.sql(
                    "CREATE TABLE " + tableName + " (",
                    "  createdDate TIMESTAMP          NOT NULL DEFAULT current_timestamp,",
                    "  project_name  VARCHAR(80)        NOT NULL,",
                    "  module_name   VARCHAR(80)        NOT NULL,",
                    "  update_name  VARCHAR(80)        NOT NULL,",
                    "  CONSTRAINT " + tableName +"_uc UNIQUE (project_name,module_name,update_name)",
                    ")");
            stat.execute();

        }

    }

    @Override
    public boolean isDone(String projectName, String moduleName, String updateName) {
        EStat stat = stat();
        return stat.sql("select * from " + tableName,
                "where project_name=:projectName and module_name=:moduleName and update_name=:updateName")
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


}
