package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.expr.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by petermuys on 2/10/16.
 */
public class TableValueToExpressions{

    static public PList<Expr> toExpr(EValTable vt){
        ETypeObject table = vt.getTable();
        try {
            return (PList<Expr>)(table.getClass().getDeclaredMethod("asValues",Object.class).invoke(null,vt.getValue()));
        } catch (Exception e) {
            throw new RuntimeException("error calling asValues on " + table.getClass(),e);
        }

    }

}
