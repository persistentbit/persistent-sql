package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 8/10/16.
 */
public class UpdateSqlBuilder {
    private final DbType    dbType;
    private final Update    update;
    private final PMap<ETypeObject,TableInst> tables;

    public UpdateSqlBuilder(DbType dbType, Update update) {
        this.dbType = dbType;
        this.update = update;
        PMap<ETypeObject,TableInst> allUsed = PMap.empty();
        allUsed.put(update.getTable(),new TableInst(update.getTable().getInstanceName(),update.getTable()));
        tables = allUsed;
    }
    private Optional<String> getTableInstance(ETypeObject obj){
        return tables.getOpt(obj).map(ti -> ti.getName());
    }

    public String generate() {
        String nl = "\r\n";
        String res = "UPDATE " + update.getTable()._getTableName() + nl;
        res += " SET ";
        PList<Tuple2<Expr,Expr>> sets = update.getSet();
        res += sets.map(t -> {
            PList<String> properties = ExprExpand.exapand(t._1)
                    .map(e ->ExprToSql.toSql(e,this::getTableInstance,dbType));
            PList<String> values = ExprExpand.exapand(t._2)
                    .map(e ->ExprToSql.toSql(e,this::getTableInstance,dbType));
            return  properties.zip(values).map(ns -> ns._2 + "=" + ns._1).toString(", ");
        }).toString(", ");
        if(update.getWhere() != null){
            res += nl + " WHERE " + ExprToSql.toSql(update.getWhere(),this::getTableInstance,dbType);
        }

        return res;
    }


}
