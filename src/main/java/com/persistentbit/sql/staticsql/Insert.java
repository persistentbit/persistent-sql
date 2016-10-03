package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * Created by petermuys on 2/10/16.
 */
public class Insert {
    private ETypeObject into;
    private PList<Expr> valueList;

    public Insert(ETypeObject into, PList<Expr> values) {
        this.into = into;
        this.valueList = values;
    }

    static public Insert into(ETypeObject into, Expr... values){
        return new Insert(into,PStream.from(values).plist());
    }

    public ETypeObject getInto() {
        return into;
    }

    public PList<Expr> getValues() {
        return valueList;
    }

    public <T> InsertWithGeneratedKeys<T> withGeneratedKeys(Expr<T> generatedKeys){
        if(valueList.size()>1){
            throw new RuntimeException("Can't get generated keys when inserting more than 1 row");
        }
        return new InsertWithGeneratedKeys<T>(this,generatedKeys);
    }

}
