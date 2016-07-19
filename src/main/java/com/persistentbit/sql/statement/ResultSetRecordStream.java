package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStreamLazy;
import com.persistentbit.sql.PersistSqlException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class ResultSetRecordStream extends PStreamLazy<Record>{
    static private Logger log = Logger.getLogger(ResultSetRecordStream.class.getName());
    private PMap<String,Integer>    fieldNameIndexes;
    private ResultSetMetaData md;
    private ResultSet rs;
    private boolean isIterating;
    public ResultSetRecordStream(ResultSet resultSet){

        this.rs = resultSet;
        fieldNameIndexes = PMap.empty();
        try {
            md = resultSet.getMetaData();

            for(int t=0; t<md.getColumnCount(); t++){
                fieldNameIndexes = fieldNameIndexes.put(md.getColumnLabel(t+1).toLowerCase(),t);
            }

        } catch (SQLException e) {
            log.severe(e.getMessage());

            throw new PersistSqlException(e);
        }
        this.isIterating =true;
    }

    @Override
    public String toString() {
        return "ResultSetRecordStream[" + fieldNameIndexes.keys().toString(",") + "]";
    }

    @Override
    public Iterator<Record> iterator() {
        if(this.isIterating == false) {
            throw new IllegalStateException("Can't iterate more than once over a ResultSet!");
        }
        this.isIterating = false;

        return new Iterator<Record>() {
            boolean first = true;
            @Override
            public boolean hasNext() {
                try {
                    if(first){
                        first = false;
                    }
                    boolean next = rs.next();
                    if(next == false){
                        rs.close();
                    }
                    return next;
                } catch (SQLException e) {
                    throw new PersistSqlException(e);
                }
            }

            @Override
            public Record next() {
                try{
                    Object[] values = new Object[md.getColumnCount()];
                    for(int t=0; t< md.getColumnCount(); t++){
                        values[t] = rs.getObject(t+1);
                    }
                    return new RecordImpl(fieldNameIndexes,values);
                }catch (SQLException e){
                    throw new PersistSqlException(e);
                }

            }
        };
    }


}
