package com.persistentbit.sql.statement;

import com.persistentbit.core.Lazy;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.dbdef.TableColDef;
import com.persistentbit.sql.dbdef.TableDef;
import com.persistentbit.sql.objectmappers.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * User: petermuys
 * Date: 16/07/16
 * Time: 15:26
 */
public class ETableStats<T>{
    static private final Logger log = Logger.getLogger(ETableStats.class.getName());
    private final SQLRunner runner;
    private final Lazy<TableDef> tableDef;
    private final ObjectRowMapper   mapper;
    private final EStatementPreparer    statementPreparer;
    private final Class<T> mappedClass;

    public ETableStats(SQLRunner runner,String tableName, Class<T> mappedClass,Function<String,TableDef> tableDefSupplier, ObjectRowMapper mapper){
        this.runner = runner;
        this.tableDef = new Lazy<>(()->tableDefSupplier.apply(tableName));
        this.mapper = mapper;
        this.statementPreparer = new EStatementPreparer(tableDefSupplier);
        this.mappedClass = mappedClass;
    }

    private String tableName() {
        return tableDef.get().getTableName();
    }

    public EJoinable<T> asJoinable(String name){
        return new EJoinable<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getSelectPart() {
                return ":" + name + ".*";
            }

            @Override
            public String getTableName() {
                return  tableName();
            }

            @Override
            public T mapRow(Record row) {
                return mapper.read(name,mappedClass,row.getSubRecord(name));
            }

            @Override
            public SQLRunner getRunner() {
                return runner;
            }

            @Override
            public String getOwnJoins() {
                return "";
            }

            @Override
            public EStatementPreparer getStatementPreparer() {
                return statementPreparer;
            }
        };
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
        public <T> T read(Class<T> cls, String name) {
            return (T)args.find(a -> a._1.equalsIgnoreCase(name)).map(a -> a._2).orElse(null);
        }

        public Optional<T> forId(Object id){
            String idName = tableDef.get().getIdCols().head().getName();
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
            String sql = "SELECT :t.* from :" + tableName() + ".as.t " + sqlRest;
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

    public int delete(T obj){
        InMemoryRow row = new InMemoryRow();
        mapper.write(obj,row);
        return deleteForId(row.read(null,tableDef.get().getIdCols().head().getName()));
    }

    public int deleteForId(Object id){
        PStream<String> ids = tableDef.get().getIdCols().map(c -> c.getName());

        String sql = "DELETE FROM " + tableDef.get().getTableName() +" WHERE " + ids.map(n-> n + "=:" + n).toString(" AND ");
        InMemoryRow row = new InMemoryRow();
        TableColDef idCol = tableDef.get().getIdCols().head();
        row.write(idCol.getName(),id);
        return runner.run(c -> {
           try(PreparedStatement stat = statementPreparer.prepare(c,sql,row) ){
               return stat.executeUpdate();
           }
        });
    }

    public int deleteAll() {
        return runner.run(c -> {
            try(PreparedStatement s = c.prepareStatement("DELETE FROM " + tableDef.get().getTableName())){
                return s.executeUpdate();
            }
        });
    }

    public int update(T obj){
        PList<String> names = tableDef.get().getCols().filter(c -> c.isAutoGen() == false && c.isId() == false).map(c -> c.getName());
        PStream<String> ids = tableDef.get().getIdCols().map(c -> c.getName());
        String sql = "UPDATE " + tableDef.get().getTableName() +
                " SET " + names.map(n -> n + "=:"+ n).toString(",") +
                " WHERE " + ids.map(n-> n + "=:" + n).toString(" AND ");
        InMemoryRow row = new InMemoryRow();
        mapper.write(obj,row);
        return runner.run(c -> {
            try (PreparedStatement stat = statementPreparer.prepare(c, sql, row)) {
                int count = stat.executeUpdate();
                return count;
            }
        });
    }


    public T insert(T obj){

        PList<String> names = tableDef.get().getCols().filter(c -> c.isAutoGen() == false).map(c -> c.getName());
        String sql = "INSERT INTO " + tableName() + " (" + names.toString(", ") + ") VALUES (" + names.map(n -> ":"+n.toLowerCase()).toString(", ") + ")";
        InMemoryRow row = new InMemoryRow();
        mapper.write(obj,row);
        return runner.run(c -> {
            boolean autoGen = tableDef.get().getAutoGenCols().isEmpty() == false;
            try(PreparedStatement stat = statementPreparer.prepare(c,sql,row, autoGen)) {
                stat.executeUpdate();
                if (autoGen == false) {
                    return obj;
                }
                Object autoGenObj;
                try (ResultSet res = stat.getGeneratedKeys()) {
                    if (res.next()) {
                        autoGenObj = res.getObject(1);
                    } else {
                        autoGenObj = null;
                    }

                }
                //Create a row mapper for the auto generated Id's
                ReadableRow newRec = new ReadableRow() {
                    @Override
                    public <T> T read(Class<T>cls,String name) {
                        if (tableDef.get().getAutoGenCols().find(tc -> tc.getName().equalsIgnoreCase(name)).isPresent()) {
                            return (T)autoGenObj;
                        }
                        return row.read(cls,name);
                    }
                };
                return (T) mapper.read(mappedClass, newRec);
            }catch (SQLException e){
                log.severe("Error executing " + sql);
                log.severe(e.getMessage());
                throw new PersistSqlException(e);
            }
        });

    }

}
