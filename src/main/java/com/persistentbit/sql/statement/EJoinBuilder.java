package com.persistentbit.sql.statement;

import com.persistentbit.core.Tuple2;

import java.util.function.BiFunction;

/**
 * @author Peter Muys
 * @since 20/07/2016
 */
public class EJoinBuilder{
    private EJoinable joinable;
    private String joinType;
    private String joinSql;
    private EJoinStats root;
    public EJoinBuilder(EJoinStats root,EJoinable joinable,String joinType){
        this.joinable = joinable;
        this.joinType = joinType;
        this.root = root;
    }

    public class WithOn{

        public  <L,R,T> EJoinStats<T> map(BiFunction<L,R,T> mapper) {

            EJoinStats.JoinElement el = new EJoinStats.JoinElement(joinable,joinType,joinSql,(BiFunction)mapper,true);
            return new EJoinStats(root.left,root.elements.plus(el),root.extraMappers);
        }
        public <L,R> EJoinStats<Tuple2<L,R>> mapTuple(){
            return map((l,r)-> new Tuple2<L,R>((L)l,(R)r));
        }

    }

    public WithOn on(String joinSql){
        this.joinSql = joinSql;
        return new WithOn();
    }
}

