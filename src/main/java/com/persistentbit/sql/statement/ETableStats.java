package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.function.NamedSupplier;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.dbdef.TableDef;
import com.persistentbit.sql.objectmappers.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.function.Function;

/**
 * User: petermuys
 * Date: 16/07/16
 * Time: 15:26
 */
public class ETableStats<T> implements Joinable {
    private final SQLRunner runner;
    private final TableDef  tableDef;
    private final ObjectRowMapper   mapper;
    private final EStatementPreparer    statementPreparer;
    private final Class<T> mappedClass;

    public ETableStats(SQLRunner runner,String tableName, Class<T> mappedClass,Function<String,TableDef> tableDefSupplier, ObjectRowMapper mapper){
        this.runner = runner;
        this.tableDef = tableDefSupplier.apply(tableName);
        this.mapper = mapper;
        this.statementPreparer = new EStatementPreparer(tableDefSupplier);
        this.mappedClass = mappedClass;
    }

    @Override
    public Class getMappedClass() {
        return mappedClass;
    }

    @Override
    public TableDef getTableDef() {
        return tableDef;
    }

    public class SelectBuilder implements SqlArguments<SelectBuilder>,ReadableRow{
        private PMap<String,Object> args = PMap.empty();
        private String sqlRest = "";

        @Override
        public SelectBuilder arg(String name, Object value) {
            args = args.put(name,value);
            return this;
        }

        @Override
        public Object read(String name) {
            return args.find(a -> a._1.equalsIgnoreCase(name)).map(a -> a._2).orElse(null);
        }

        public Optional<T> forId(Object id){
            String idName = tableDef.getIdCols().head().getName();
            sqlRest(" where t." + idName+" = :id");
            arg("id",id);
            return getOne();
        }

        public SelectBuilder sqlRest(String sql){
            sqlRest = sql;
            return this;
        }
        public Optional<T> getOne(){
            return visit(s -> s.headOpt());
        }
        public PList<T>    getList() {
            return visit(s -> s.plist());

        }
        private <RES> RES visit(Function<PStream<T>,RES> streamReader){
            String sql = "SELECT :t.* from :" + tableDef.getTableName() + ".as.t " + sqlRest;
            return runner.run(c -> {
                try(PreparedStatement stat = statementPreparer.prepare(c,sql,SelectBuilder.this)) {

                    ResultSetRecordStream rs = new ResultSetRecordStream(stat.executeQuery());

                    return streamReader.apply(rs.map(r -> (T) mapper.read(mappedClass, r.getSubRecord("t"))));
                }
            });
        }



    }
    public SelectBuilder select() {
        return new SelectBuilder();
    }
    public SelectBuilder select(String sqlRest){
        return select().sqlRest(sqlRest);
    }

    public T insert(T obj){

        PList<String> names = tableDef.getCols().filter(c -> c.isAutoGen() == false).map(c -> c.getName());
        String sql = "INSERT INTO " + tableDef.getTableName() + " (" + names.toString(", ") + ") VALUES (" + names.map(n -> ":"+n.toLowerCase()).toString(", ") + ")";
        InMemoryRow row = new InMemoryRow();
        mapper.write(obj,row);
        return runner.run(c -> {
            boolean autoGen = tableDef.getAutoGenCols().isEmpty() == false;
            PreparedStatement stat = statementPreparer.prepare(c,sql,row, autoGen);
            stat.executeUpdate();
            if(autoGen == false){
                return obj;
            }
            Object autoGenObj ;
            try(ResultSet res = stat.getGeneratedKeys()){
                if(res.next()) {
                    autoGenObj = res.getObject(1);
                } else {
                    autoGenObj = null;
                }

            }
            //Create a row mapper for the auto generated Id's
            ReadableRow newRec = new ReadableRow() {
                @Override
                public Object read(String name) {
                    if(tableDef.getAutoGenCols().find(tc -> tc.getName().equalsIgnoreCase(name)).isPresent()){
                        return autoGenObj;
                    }
                    return row.read(name);
                }
            };
            return (T)mapper.read(mappedClass,newRec);
        });

    }

}
