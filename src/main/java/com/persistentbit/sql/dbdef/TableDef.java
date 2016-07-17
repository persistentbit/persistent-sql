package com.persistentbit.sql.dbdef;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Peter Muys
 * @since 14/07/2016
 */
public class TableDef {
    private final String tableName;
    private final PList<TableColDef> cols;

    public TableDef(String tableName, PList<TableColDef> cols) {
        this.tableName = tableName;
        this.cols = cols;
    }

    public String getTableName() {
        return tableName;
    }

    public PList<TableColDef> getCols() {
        return cols;
    }
    public PStream<TableColDef> getIdCols() {
        return cols.filter(c -> c.isId());
    }
    public PStream<TableColDef> getAutoGenCols() {
        return cols.filter(c-> c.isAutoGen());
    }

    @Override
    public String toString() {
        return tableName + getCols().toString();
    }


    public static TableDef  getFromDb(String tableName, Connection c){
        Set<String> primKeys = new HashSet<>();
        PList<TableColDef> res = PList.empty();
        try {
            DatabaseMetaData metaData = c.getMetaData();
            try(ResultSet rs = metaData.getPrimaryKeys(null,null,tableName)){
                while(rs.next()){
                    primKeys.add(rs.getString("COLUMN_NAME"));
                }
            }
            try(ResultSet rs = metaData.getColumns(null,null,tableName,"%")){
                while(rs.next())
                {

                    String name = rs.getString("COLUMN_NAME");

                    boolean isAutoIncrement = rs.getString("IS_AUTOINCREMENT").equals("YES");

                    res = res.plus(new TableColDef(name,primKeys.contains(name),isAutoIncrement));
                }
            }

            return new TableDef(tableName,res);
        }catch(SQLException e){
            throw new PersistSqlException(e);
        }
    }
}
