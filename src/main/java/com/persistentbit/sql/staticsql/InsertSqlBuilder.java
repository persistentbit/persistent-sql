package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 2/10/16.
 */
public class InsertSqlBuilder {
    private final DbType    dbType;
    private final Insert    insert;
    private final Expr      generatedKeys;

    public InsertSqlBuilder(DbType dbType, Insert insert,Expr generatedKeys) {
        this.dbType = dbType;
        this.insert = insert;
        this.generatedKeys = generatedKeys;
    }
    public InsertSqlBuilder(DbType dbType, Insert insert){
        this(dbType,insert,null);
    }

    public String generate() {
        String nl = "\r\n";
        String res = "";
        String tableName = insert.getInto()._getTableName();
        res += "INSERT INTO " + insert.getInto()._getTableName() + " ";
        PList<Tuple2<String,Expr>> all = insert.getInto()._all();
        PList<Expr> expanded = all.map(e -> ExprExpand.exapand(e._2)).<Expr>flatten().plist();
        PList<Expr> expandedGenerated = ExprExpand.exapand(generatedKeys);
        PList<Expr> expandedNotGenerated = expanded.filter( e -> expandedGenerated.contains(e) == false);

        PList<String> names = expandedNotGenerated.map(e -> ExprToSql.toSql(e,t-> Optional.of(t._getTableName()),dbType)).map(n -> n.substring(tableName.length()+1));
        res += "(" + names.toString(", ") + ")" + nl;
        res += "VALUES \r\n";
        res += insert.getValues().map(v -> {
            PList<Expr> vals = ExprExpand.exapand(v);
            PList<Expr> valsNoGen = PList.empty();
            for(int t=0; t<expanded.size();t++){
                boolean generated = expandedGenerated.contains(expanded.get(t));
                if(generated == false){
                    valsNoGen = valsNoGen.plus(vals.get(t));
                }
            }
            return valsNoGen.map(vn->ExprToSql.toSql(vn,t -> Optional.of(t._getTableName()),dbType)).toString("(",", ", ")");
        }).toString(",\r\n");


        //res += insert.getValues().map(v -> ExprToSql.toSql(v,t -> Optional.of(t._getTableName()),dbType)).toString(",\r\n");
        return res.toString();
    }
}
