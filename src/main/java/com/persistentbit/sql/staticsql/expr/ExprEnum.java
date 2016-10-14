package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 5/10/16.
 */
public class ExprEnum<T extends Enum<T>> implements ETypeEnum<T> {
    private T value;
    private Class<T> enumClass;

    public ExprEnum(T value,Class<? extends Enum> enumClass) {
        this.value = value;
        this.enumClass = (Class<T>)enumClass;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return (R) visitor.visit(this);
    }

    public T getValue() {
        return value;
    }

    @Override
    public Class<T> _getEnumClass() {
        return enumClass;
    }
}
