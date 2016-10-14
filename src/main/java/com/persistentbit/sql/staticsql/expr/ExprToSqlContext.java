package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.sql.databases.DbType;

/**
 * Created by petermuys on 14/10/16.
 */
public class ExprToSqlContext {
    private int nextUniqueId = 1;
    private PMap<Expr,String>   instanceNameLookup  =   PMap.empty();
    private final DbType    dbType;

    public ExprToSqlContext(DbType dbType) {
        this.dbType = dbType;
    }

    public String   uniqueInstanceName(Expr expr, String defaultName){
        String res = instanceNameLookup.getOrDefault(expr,null);
        if(res == null){
            res = "_" + defaultName + "_" + nextUniqueId;
            nextUniqueId++;
            instanceNameLookup = instanceNameLookup.put(expr,res);
        }
        return res;
    }

    public DbType getDbType() {
        return dbType;
    }
}
