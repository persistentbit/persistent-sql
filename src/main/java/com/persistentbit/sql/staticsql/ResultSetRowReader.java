package com.persistentbit.sql.staticsql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by petermuys on 3/10/16.
 */
public class ResultSetRowReader implements RowReader{
    private final ResultSet rs;
    private int index = 1;
    public ResultSetRowReader(ResultSet rs) {
        this.rs = rs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readNext(Class<T> cls) {
        try{
            if(cls.equals(LocalDate.class)){
                Date dt = rs.getObject(index++,Date.class);
                if(dt == null) { return null; }

                return (T)dt.toLocalDate();
            } else if(cls.equals(LocalDateTime.class)){
                Timestamp ts = rs.getObject(index++,Timestamp.class);
                if(ts == null) { return null; }
                return (T)ts.toLocalDateTime();
            }

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
