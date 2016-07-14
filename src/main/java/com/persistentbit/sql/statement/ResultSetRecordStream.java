package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class ResultSetRecordStream implements PStream<Record>{
    private PMap<String,Integer>    fieldNameIndexes;
    private ResultSetMetaData md;
    private ResultSet rs;
    private boolean firstIter = true;
    public ResultSetRecordStream(ResultSet resultSet){
        try {
            this.rs = resultSet;
            md = resultSet.getMetaData();
            PMap<String,Integer> fieldNameIndexes = PMap.empty();
            for(int t=0; t<md.getColumnCount(); t++){
                fieldNameIndexes = fieldNameIndexes.put(md.getColumnLabel(t+1).toLowerCase(),t);
            }
            this.fieldNameIndexes = fieldNameIndexes;
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }
    }


    @Override
    public Iterator<Record> iterator() {
        if(firstIter == false) {
            throw new IllegalStateException("Can't iterate mode than once over a ResultSet!");
        }
        firstIter = false;
        return new Iterator<Record>() {
            @Override
            public boolean hasNext() {
                try {
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
