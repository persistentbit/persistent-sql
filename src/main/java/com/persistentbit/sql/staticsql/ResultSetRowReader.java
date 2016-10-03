package com.persistentbit.sql.staticsql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by petermuys on 3/10/16.
 */
public class ResultSetRowReader implements RowReader{
    private final ResultSet rs;
    private int index = 1;
    public ResultSetRowReader(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public <T> T readNext(Class<T> cls) {
        try{
            return rs.getObject(index++,cls);
        }catch(SQLException e){
            throw new RuntimeException("SQL error while reading column " + index + " from resultset",e);
        }

    }
    public ResultSetRowReader nextRow(){
        index = 1;
        return this;
    }
}
