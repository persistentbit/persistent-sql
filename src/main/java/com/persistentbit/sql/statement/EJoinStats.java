package com.persistentbit.sql.statement;

import com.persistentbit.core.Immutable;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.dbdef.TableDef;
import com.persistentbit.sql.lazy.LazyLoadingRef;
import com.persistentbit.sql.lazy.LazyPStream;
import com.persistentbit.sql.objectmappers.ReadableRow;
import com.persistentbit.core.references.RefId;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by petermuys on 16/07/16.
 */
@Immutable
public class EJoinStats<R> {

    static class JoinElement{
        public final EJoinable joinable;
        public final String joinType;
        public final String joinSQL;
        public final BiFunction<Object,Object,Object> mapper;
        public final boolean select;

        public JoinElement(EJoinable joinable, String joinType, String joinSQL,BiFunction<Object,Object,Object> mapper,boolean select) {
            this.joinable = joinable;
            this.joinType = joinType;
            this.joinSQL = joinSQL;
            this.mapper = mapper;
            this.select = select;
        }

    }
    final EJoinable left;
    final PList<JoinElement> elements;
    final PList<Function> extraMappers;



    public EJoinStats(EJoinable<R> left){
        this(left,PList.empty(),PList.empty());
    }

    EJoinStats(EJoinable left, PList<JoinElement> elements,PList<Function> extraMappers) {
        this.left  = left;
        this.elements = elements;
        this.extraMappers = extraMappers;
    }



    public EJoinable<R> asJoinable() {
        return new EJoinable<R>() {

            @Override
            public String getName() {
                return left.getName();
            }

            @Override
            public String getTableName() {
                return left.getTableName();
            }


            @Override
            public String getSelectPart() {
                return left.getSelectPart() + ", " + elements.map(e-> e.joinable.getSelectPart()).toString(", ");
            }

            @Override
            public String getOwnJoins() {
                //return elements.map(e-> e.joinable.getOwnJoins()).toString(" ");
                return " " + elements.map(e -> e.joinType + " :" + e.joinable.getTableName() + ".as." + e.joinable.getName() + " ON " + e.joinSQL + e.joinable.getOwnJoins()).toString(" " );
            }

            @Override
            public R mapRow(Record r) {
                Object prev = left.mapRow(r);
                for(JoinElement je : elements){
                    Object right =  je.joinable.mapRow(r);
                    prev = je.mapper.apply(prev,right);
                }
                for(Function mapper : extraMappers){
                    prev = mapper.apply(prev);
                }
                return (R)prev;
            }

            @Override
            public SQLRunner getRunner() {
                return left.getRunner();
            }

            @Override
            public EStatementPreparer getStatementPreparer() {
                return left.getStatementPreparer();
            }

            @Override
            public DbType getDbType() {
                return left.getDbType();
            }

            @Override
            public TableDef getTableDef() {
                return left.getTableDef();
            }
        };
    }

    public <X> EJoinStats<X> extraMapping(Function<R,X> mapper){
        return new EJoinStats<X>(left,elements,extraMappers.plus(mapper));
    }



    public EJoinBuilder leftJoin(ETableStats table,String as){
        return leftJoin(table.asJoinable(as));
    }
    public EJoinBuilder rightJoin(ETableStats table,String as){
        return rightJoin(table.asJoinable(as));
    }
    public EJoinBuilder innerJoin(ETableStats table,String as){
        return innerJoin(table.asJoinable(as));
    }
    public EJoinBuilder fullOuterJoin(ETableStats table,String as){
        return fullOuterJoin(table.asJoinable(as));
    }

    public EJoinBuilder leftJoin(EJoinable joinable){
        return new EJoinBuilder(this,joinable,"LEFT OUTER JOIN");
    }
    public EJoinBuilder rightJoin(EJoinable joinable){
        return new EJoinBuilder(this,joinable,"RIGHT OUTER JOIN");
    }

    public EJoinBuilder innerJoin(EJoinable joinable){
        return new EJoinBuilder(this,joinable,"INNER JOIN");
    }
    public EJoinBuilder fullOuterJoin(EJoinable joinable){
        return new EJoinBuilder(this,joinable,"FULL OUTER JOIN");
    }

    public EJoinBuilder leftJoin(EJoinStats<R> join){
        return leftJoin(join.asJoinable());
    }
    public EJoinBuilder rightJoin(EJoinStats<R> join){
        return rightJoin(join.asJoinable());
    }
    public EJoinBuilder innerJoin(EJoinStats<R> join){
        return innerJoin(join.asJoinable());
    }
    public EJoinBuilder fullOuterJoin(EJoinStats<R> join){
        return fullOuterJoin(join.asJoinable());
    }



    public EJoinStats<R> merge(EJoinStats<R> other){
        if(other.left.getName().equals(left.getName()) &&
                other.left.getTableName().equals(left.getTableName())){
            return new EJoinStats(this.left,this.elements.plusAll(other.elements),this.extraMappers.plusAll(other.extraMappers));
        }

        throw new RuntimeException("Not Yet implemented");
    }


    public class SelectBuilder implements SqlArguments<SelectBuilder>, ReadableRow {
        private PMap<String, Object> args = PMap.empty();
        private String sqlRest = "";
        private Long limit = null;
        private Long offset = null;

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
            args = args.put(name, value);
            return this;
        }

        @Override
        public <T> T read(Class<T>cls, String name) {
            return ReadableRow.check(cls,name,(T)args.find(a -> a._1.equalsIgnoreCase(name)).map(a -> a._2).orElse(null));

        }

        /**
         * Select for the root table with the provided id
         * @param id The id of the root table
         * @return Optional joined result for the provided id
         */
        public Optional<R> forId(Object id){
            String idName = left.getTableDef().getIdCols().head().getName();
            sqlRest(" where " + left.getName() + "." +  idName+" = :id");
            arg("id",id);
            return getOne();
        }

        public <ID> LazyLoadingRef<R,ID> lazyLoadingRef(ID id){
            return new LazyLoadingRef<R, ID>(new RefId<R, ID>(id),() -> forId(id).orElseThrow(() -> new PersistSqlException("Can't get " + left.getTableDef().getTableName() + " with id " + id)));
        }


        public SelectBuilder sqlRest(String sql) {
            sqlRest = sql;
            return this;
        }

        public Optional<R> getOne() {
            return left.getRunner().run(c -> {
                return visit(v -> v.headOpt());
            });
        }

        public PList<R> getList() {
            return visit(s -> s.plist());
        }

        /**
         * Create a PStream that is loaded the first time it is accessed.
         * @return The Lazy Loading PStream
         */
        public <T> LazyPStream<T> lazyLoading() {
            SelectBuilder copy = new SelectBuilder(args,sqlRest,limit,offset);
            return new LazyPStream<T>(() -> (PStream<T>)copy.getList());
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

        private <X> X visit(Function<PStream<R>,X> visitor) {
            return left.getRunner().run(c -> {
                String elementsSelect = elements.isEmpty() ? "" : "," + elements.map(e-> e.joinable.getSelectPart()).toString(", ");
                String sql = "SELECT " + left.getSelectPart() + elementsSelect + " FROM :" + left.getTableName() + ".as." + left.getName()
                        + " " + elements.map(e -> e.joinType + " :" + e.joinable.getTableName() + ".as." + e.joinable.getName() + " ON " + e.joinSQL + e.joinable.getOwnJoins()).toString(" " ) + " " + sqlRest;

                if(limit != null){
                    if(offset != null){
                        sql = left.getDbType().sqlWithLimitAndOffset(limit,offset,sql);
                    } else {
                        sql = left.getDbType().sqlWithLimit(limit,sql);
                    }
                }
                try (PreparedStatement stat = left.getStatementPreparer().prepare(c, sql, SelectBuilder.this)) {

                    ResultSetRecordStream rs = new ResultSetRecordStream(stat.executeQuery());
                    PStream<R> trs = rs.map(r -> {
                        Object prev = left.mapRow(r);
                        for(JoinElement je : elements){
                            Object right =  je.joinable.mapRow(r);
                            prev = je.mapper.apply(prev,right);
                        }
                        for(Function mapper : extraMappers){
                            prev = mapper.apply(prev);
                        }
                        return (R)prev;
                    });
                    return visitor.apply(trs);
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

}
