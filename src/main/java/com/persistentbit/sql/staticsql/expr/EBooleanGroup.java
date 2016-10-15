package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 5/10/16.
 */
public class EBooleanGroup extends EGroup<Boolean> implements ETypeBoolean{
    public EBooleanGroup(Expr<Boolean> value) {
        super(value);
    }


}
