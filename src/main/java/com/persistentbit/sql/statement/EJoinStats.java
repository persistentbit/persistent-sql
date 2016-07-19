package com.persistentbit.sql.statement;

import com.persistentbit.core.Immutable;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.objectmappers.ReadableRow;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by petermuys on 16/07/16.
 */
@Immutable
public class EJoinStats {

    static private class JoinElement{
        public final EJoinable joinable;
        public final String joinType;
        public final String joinSQL;
        public final Function<PList<Object>,PList<Object>> mapper;
        public final boolean select;

        public JoinElement(EJoinable joinable, String joinType, String joinSQL,Function<PList<Object>,PList<Object>> mapper,boolean select) {
            this.joinable = joinable;
            this.joinType = joinType;
            this.joinSQL = joinSQL;
            this.mapper = mapper;
            this.select = select;
        }
        public JoinElement withMapper(Function<PList<Object>,PList<Object>> mapper){
            return new JoinElement(joinable,joinType,joinSQL, mapper,select);
        }
        public JoinElement withSelect(boolean select) {
            return new JoinElement(joinable,joinType,joinSQL,mapper,select);
        }
    }
    private final EJoinable left;
    private final PList<JoinElement> elements;



    public EJoinStats(EJoinable left, EJoinable right, String joinType,String joinSql){
        this(left,right,joinType,joinSql,t->t);
    }

    public EJoinStats(EJoinable left, EJoinable right, String joinType,String joinSql,Function<PList<Object>,PList<Object>>mapper){
        this(left,PList.val(new JoinElement(right,joinType,joinSql,mapper,true)));
    }

    public EJoinStats(EJoinable left){
        this(left,PList.empty());
    }

    private EJoinStats(EJoinable left, PList<JoinElement> elements) {
        this.left  = left;
        this.elements = elements;
    }


    public EJoinable asJoinable() {
        return new EJoinable() {

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
                return elements.map(e-> e.joinable.getOwnJoins()).toString(" ");
            }

            @Override
            public PList<Object> mapRow(Record row) {
                return PList.empty().plusAll(left.mapRow(row)).plusAll(elements.map(e->e.joinable.mapRow(row)));
            }

            @Override
            public SQLRunner getRunner() {
                return left.getRunner();
            }

            @Override
            public EStatementPreparer getStatementPreparer() {
                return left.getStatementPreparer();
            }
        };
    }

    public class EJoinStatJoin{
        private JoinElement element;
        private EJoinStatJoin(JoinElement element){
            this.element = element;
        }

        public <X,Y> EJoinStatJoin    mapLastItems(BiFunction<X,Y,Object> mapper){
            Function<PList<Object>,PList<Object>> fullMapper = list -> {
                try {

                    Y right = (Y) list.lastOpt().orElse(null);
                    PList<Object> res = list.dropLast();
                    X left = (X) res.lastOpt().orElse(null);
                    res = res.dropLast();
                    Object value = mapper.apply(left, right);
                    return res.plus(value);
                }catch(Exception e){
                    throw new PersistSqlException(e);
                }
            };
            JoinElement el = element.withMapper(fullMapper);
            return new EJoinStats(left,elements.replaceFirst(element,el)).getJoin(el);
        }

        public EJoinStats get(){
            return EJoinStats.this;
        }
        public EJoinStatJoin  fullOuterJoin(EJoinable other,String joinSql){
            return EJoinStats.this.fullOuterJoin(other,joinSql);
        }
        public EJoinStatJoin  leftOuterJoin(EJoinable other,String joinSql){
            return EJoinStats.this.leftOuterJoin(other,joinSql);
        }
        public EJoinStatJoin  rightOuterJoin(EJoinable other,String joinSql){
            return EJoinStats.this.rightOuterJoin(other,joinSql);
        }
        public EJoinStatJoin  innerJoin(EJoinable other,String joinSql){
            return EJoinStats.this.innerJoin(other,joinSql);
        }
    }

    private EJoinStatJoin getJoin(JoinElement el){
        return new EJoinStatJoin(el);
    }


    public EJoinStatJoin  fullOuterJoin(EJoinable other,String joinSql){
        return join("FULL OUTER JOIN",other,joinSql,t->t);
    }
    public EJoinStatJoin  leftOuterJoin(EJoinable other,String joinSql){
        return join("LEFT OUTER JOIN",other,joinSql,t->t);
    }
    public EJoinStatJoin  rightOuterJoin(EJoinable other,String joinSql){
        return join("RIGHT OUTER JOIN",other,joinSql,t->t);
    }
    public EJoinStatJoin  innerJoin(EJoinable other,String joinSql){
        return join("INNER JOIN",other,joinSql,t->t);
    }

    public EJoinStatJoin join(String joinType,EJoinable other,String joinSql,Function<PList<Object>,PList<Object>>mapper){
        JoinElement el = new JoinElement(other,joinType,joinSql,mapper,true);
        return new EJoinStats(left,elements.plus(el)).getJoin(el);
    }

    public class SelectBuilder implements SqlArguments<SelectBuilder>, ReadableRow {
        private PMap<String, Object> args = PMap.empty();
        private String sqlRest = "";

        @Override
        public SelectBuilder arg(String name, Object value) {
            args = args.put(name, value);
            return this;
        }

        @Override
        public <T> T read(Class<T>cls, String name) {
            return ReadableRow.check(cls,name,(T)args.find(a -> a._1.equalsIgnoreCase(name)).map(a -> a._2).orElse(null));

        }


        public SelectBuilder sqlRest(String sql) {
            sqlRest = sql;
            return this;
        }

        public Optional<PList<Object>> getOne() {
            return left.getRunner().run(c -> {
                return visit(v -> v.headOpt());
            });
        }

        public PList<PList<Object>> getList() {
            return visit(s -> s.plist());
        }



        private <X> X visit(Function<PStream<PList<Object>>,X> visitor) {
            return left.getRunner().run(c -> {

                String sql = "SELECT " + left.getSelectPart() + "," + elements.map(e-> e.joinable.getSelectPart()).toString(", ") + " FROM :" + left.getTableName() + ".as." + left.getName()
                        + " " + elements.map(e -> e.joinType + " :" + e.joinable.getTableName() + ".as." + e.joinable.getName() + " ON " + e.joinSQL + e.joinable.getOwnJoins()).toString(" " ) + " " + sqlRest;

                try (PreparedStatement stat = left.getStatementPreparer().prepare(c, sql, SelectBuilder.this)) {

                    ResultSetRecordStream rs = new ResultSetRecordStream(stat.executeQuery());
                    PStream<PList<Object>> trs = rs.map(r -> {
                        PList<Object> all = PList.empty().plus(left.mapRow(r).head());
                        for(JoinElement je : elements){
                            all = all.plusAll(je.joinable.mapRow(r));
                            all = je.mapper.apply(all);
                        }
                        return all;
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
