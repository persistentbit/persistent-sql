package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprConstNumber<N extends Number> implements ETypeNumber<N>{
    private final N value;

    public ExprConstNumber(N value) {
         this.value = value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
}
