package com.persistentbit.sql.statement;

import com.persistentbit.core.Lazy;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.dbdef.TableColDef;
import com.persistentbit.sql.dbdef.TableDef;
import com.persistentbit.sql.lazy.LazyLoadingRef;
import com.persistentbit.sql.lazy.LazyPStream;
import com.persistentbit.sql.objectmappers.InMemoryRow;
import com.persistentbit.sql.objectmappers.ObjectRowMapper;
import com.persistentbit.sql.objectmappers.ReadableRow;
import com.persistentbit.core.references.RefId;
import com.persistentbit.sql.statement.annotations.DbTableName;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A class to automaticly handle sql access to 1 database table mapped to/from 1 java object.<br>
 * @param <T> The Type of the mapped Java Object
 */
public class ETableStats<T>{

    static private final Logger log = Logger.getLogger(ETableStats.class.getName());

    private final SQLRunner             runner;
    private final Lazy<TableDef>        tableDef;
    private final ObjectRowMapper       mapper;
    private final EStatementPreparer    statementPreparer;
    private final Class<T>              mappedClass;
    private final DbType                dbType;

    /**
     * Create a new Instance
     * @param runner The Transaction runner
     * @param mappedClass The class of the Java mapped object. The class name or {@link com.persistentbit.sql.statement.annotations.DbTableName} annotation will be used to define the tablename
     * @param tableDefSupplier A supplier of Table definitions
     * @param mapper The Mapper between database rows and java objects
     * @param dbType The database type instance.
     */
    public ETableStats(SQLRunner runner,Class<T> mappedClass,Function<String,TableDef> tableDefSupplier, ObjectRowMapper mapper,DbType dbType){
        this.runner = Objects.requireNonNull(runner);
        String name = mappedClass.getSimpleName();
        DbTableName tn = mappedClass.getAnnotation(DbTableName.class);
        if(tn != null) { name = tn.value(); }
        String tableName = name;
        this.tableDef = new Lazy<>(()->tableDefSupplier.apply(tableName));
        this.mapper = mapper;
        this.statementPreparer = new EStatementPreparer(tableDefSupplier);
        this.mappedClass = mappedClass;
        this.dbType = dbType;
    }

    /**
     * Create a new Instance
     * @param runner The Transaction runner
     * @param tableName The name of the database table
     * @param mappedClass The class of the Java mapped object
     * @param tableDefSupplier A supplier of Table definitions
     * @param mapper The Mapper between database rows and java objects
     * @param dbType The database type instance.
     */
    public ETableStats(SQLRunner runner,String tableName, Class<T> mappedClass,Function<String,TableDef> tableDefSupplier, ObjectRowMapper mapper,DbType dbType){
        this.runner = Objects.requireNonNull(runner);
        this.tableDef = new Lazy<>(()->tableDefSupplier.apply(tableName));
        this.mapper = mapper;
        this.statementPreparer = new EStatementPreparer(tableDefSupplier);
        this.mappedClass = mappedClass;
        this.dbType = dbType;
    }



    private String tableName() {
        return tableDef.get().getTableName();
    }

    /**
     * Use this table as the Root table for a SQL Join
     * @param name Table instance name to use
     * @return Join Structure
     */
    public EJoinStats startJoin(String name){
        return new EJoinStats(asJoinable(name));
    }

    /**
     * Create a joinable instance of the table
     * @param name Table instance name to use
     * @return EJoinable instance of this
     */
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
            public TableDef getTableDef() {
                return tableDef.get();
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

            @Override
            public DbType getDbType() {
                return dbType;
            }
        };
    }


    /**
     * SQL Select query builder
     */
    public class SelectBuilder implements SqlArguments<SelectBuilder>{
        private PMap<String,Object> args = PMap.empty();
        private String sqlRest = "";
        private Long limit;
        private Long offset;


        public SelectBuilder() {

        }

        public SelectBuilder(PMap<String, Object> args, String sqlRest, Long limit, Long offset) {
            this.args = args;
            this.sqlRest = sqlRest;
            this.limit = limit;
            this.offset = offset;
        }

        @Override
        public SelectBuilder arg(String name, Object value) {
            args = args.put(name,value);
            return this;
        }




        /**
         * Select a record by Id.<br>
         * Only works if the KEY of the table is not more than 1  column
         * @param id The java id
         * @return Optional of the mapped record
         */
        public Optional<T> forId(Object id){
            String idName = tableDef.get().getIdCols().head().getName();
            sqlRest(" where t." + idName+" = :id");
            arg("id",id);
            return getOne();
        }

        public <ID> LazyLoadingRef<T,ID> lazyLoadingRef(ID id){
            return new LazyLoadingRef<T, ID>(new RefId<T, ID>(id),() -> forId(id).orElseThrow(() -> new PersistSqlException("Can't get " + mappedClass.getSimpleName() + " with id " + id)));
        }

        /**
         * Add extra SQL to this sql statement.<br>
         * Example: sqlRest("where t.name=:name order by date")
         * @param sql The extra SQL code that will be added to the sql string after the table name
         * @return The Select builder
         */
        public SelectBuilder sqlRest(String sql){
            sqlRest = sql;
            return this;
        }

        /**
         * Execute the select and return the mapped first row
         * @return The optional mapped first row
         */
        public Optional<T> getOne(){
            return visit(s -> s.headOpt());
        }

        /**
         * Execute the select and return all mapped rows
         * @return Persistent list of all mapped rows
         */
        public PList<T>    getList() {
            return visit(s -> s.plist());
        }

        /**
         * Create a PStream that is loaded the first time it is accessed.
         * @return The Lazy Loading PStream
         */
        public LazyPStream<T> lazyLoading() {
            SelectBuilder copy = new SelectBuilder(args,sqlRest,limit,offset);
            return new LazyPStream<>(() -> copy.getList());
        }




        /**
         * Change to SQL select to limit the number of rows selected
         * @param limit Maximum number of rows to select
         * @return This select builder
         */
        public SelectBuilder limit(long limit){
            this.limit = limit;
            return this;
        }

        /**
         * Change to SQL select to limit the number of rows selected and from what row to start
         * @param limit Maximum number of rows to select
         * @param offset index of the first row returned (starting from 0)
         * @return This select builder
         */
        public SelectBuilder limitAndOffset(long limit,long offset){
            this.limit = limit;
            this.offset = offset;
            return this;
        }


        private <RES> RES visit(Function<PStream<T>,RES> streamReader){
            String sql = "SELECT :t.* from :" + tableName() + ".as.t " + sqlRest;

            if(limit != null){
                if(offset != null){
                    sql = dbType.sqlWithLimitAndOffset(limit,offset,sql);
                } else {
                    sql = dbType.sqlWithLimit(limit,sql);
                }
            }

            ReadableRow rrow = new ReadableRow() {
                @Override
                public <T> T read(Class<T> cls, String name) {
                    return ReadableRow.check(cls,name,(T)args.find(a -> a._1.equalsIgnoreCase(name)).map(a -> a._2).orElse(null));
                }
            };
            String finalSql = sql;
            return runner.run(c -> {
                try(PreparedStatement stat = statementPreparer.prepare(c,finalSql,rrow)) {

                    ResultSetRecordStream rs = new ResultSetRecordStream(stat.executeQuery());

                    return streamReader.apply(rs.map(r -> (T) mapper.read(mappedClass, r.getSubRecord("t"))));
                }
            });
        }



    }


    /**
     * Create a new SQL select builder
     * @return The SQL select builder
     */
    public SelectBuilder select() {
        return new SelectBuilder();
    }

    /**
     * Create a new SQL select builder and initialize the sqlRest code.
     * @see SelectBuilder#sqlRest(String)
     * @param sqlRest example "where t.name=:name order by date"
     * @return The SQL select builder
     */
    public SelectBuilder select(String sqlRest){
        return select().sqlRest(sqlRest);
    }

    /**
     * delete the database row corresponding to this mapped object.
     * @param obj The mapped row to delete
     * @return The number of rows deleted.
     */
    public int delete(T obj){
        InMemoryRow row = new InMemoryRow();
        mapper.write(obj,row);
        return deleteForId(row.read(null,tableDef.get().getIdCols().head().getName()));
    }

    /**
     * Delete the table row with given id
     * @param id The id of the row
     * @return The number of rows deleted.
     */
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

    /**
     * Delete all the rows in this table
     * @return The number of rows deleted
     */
    public int deleteAll() {
        return runner.run(c -> {
            try(PreparedStatement s = c.prepareStatement("DELETE FROM " + tableDef.get().getTableName())){
                return s.executeUpdate();
            }
        });
    }


    /**
     * Update the row in this table
     * @param obj The mapped existing row to update in the table
     * @return The number of rows updated
     */
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

    /**
     * Insert a new row in the table.<br>
     *
     * @param obj The object to insert
     * @return The object that was inserted initialized with any autogenerated keys.
     */
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
                            T result = (T)autoGenObj;
                            return ReadableRow.checkAndConvert(cls,name,result);
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

    /**
     * Insert a new or update an existing row in the table
     * @param obj The mapped object to insert or update
     * @return The object that was inserted or updated, initialized with any autogenerated keys.
     */
    public T save(T obj){
        if(update(obj) == 1){
            return obj;
        }
        return insert(obj);
    }
}
