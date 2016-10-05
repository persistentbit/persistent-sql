package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 5/10/16.
 */
public class ExprEnum<T extends Enum<?>> implements ETypeEnum<T> {
    private T value;
    private Class<?> cls;

    public ExprEnum(T value,Class<?> cls) {
        this.value = value;
        this.cls = cls;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return (R) visitor.visit(this);
    }

    public T getValue() {
        return value;
    }

    public Class<?> getCls() {
        return cls;
    }
}
