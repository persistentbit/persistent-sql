package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.ETypeSelection;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 2/10/16.
 */
public class QuerySqlBuilder {

    private final ETypeSelection s;
    private final Query q;
    private final DbType type;
    private final PMap<ETypeObject,TableInst> tables;
    public QuerySqlBuilder(ETypeSelection s, DbType type){
        this.s = s;
        this.q = s.getQuery();
        this.type = type;
        PSet<ETypeObject> allUsedObjects = ExprFindAllUsedTables.findAll(s.getSelection());
        if(q.getWhere().isPresent()) {
            allUsedObjects.plusAll(q.getWhere().map(w -> ExprFindAllUsedTables.findAll(w)).get());
        }
        allUsedObjects.plusAll(q.getJoins().map(j -> ExprFindAllUsedTables.findAll(j.getJoinExpr().orElse(null))).flatten());
        PSet<ETypeObject> allDefinedObjects = PSet.val(q.getFrom()).plusAll(q.getJoins().map(j-> j.getTable()));
        PSet<TableInst> allTables = allUsedObjects.plusAll(allDefinedObjects).zipWithIndex().map(t -> new TableInst(t._2.getInstanceName()+t._1,t._2)).pset();
        tables = PMap.<ETypeObject,TableInst>empty().plusAll(allTables.map(ti -> Tuple2.of(ti.getTable(),ti)));
        //System.out.println("All tables: " + tables.toString(", "));
    }

    private String toSql(Expr e){
        return ExprToSql.toSql(e,this::getTableName,type);
    }

    private Optional<String> getTableName(ETypeObject typeObject){
        Optional<String> result = tables.getOpt(typeObject).map(t -> t.getName());
        return result;
    }

    public String generate() {
        PList<Expr> expanded = ExprExpand.exapand(s.getSelection());
        //System.out.println("Expanded selection: " + expanded);
        PList<String> asSql = expanded.map(this::toSql);
        asSql = asSql.zipWithIndex().map(t -> t._2 + " AS " + "t_" + (t._1+1)).plist();
        String nl = "\r\n";
        String sql = "SELECT " + asSql.toString(", ") + nl;
        sql += " FROM " + q.getFrom()._getTableName() + " AS " + tables.get(q.getFrom()).getName() + nl;
        sql += q.getJoins().map(j -> joinToString(j)).toString(nl);
        sql += q.getWhere().map(w -> nl + "WHERE " + toSql(w)).orElse("");

        return sql;
    }
    private String joinToString(Join join){
        String res = "";
        switch(join.getType()){
            case full: res += "FULL JOIN";break;
            case inner: res += "INNER JOIN"; break;
            case left: res += "LEFT JOIN"; break;
            case right: res += "RIGHT JOIN"; break;
            default: throw new IllegalArgumentException(join.getType().toString());

        }
        res += " " + join.getTable()._getTableName() + " " + getTableName(join.getTable()).get();
        res += join.getJoinExpr().map( e -> " ON " + toSql(e)).get();
        return res;
    }
}
