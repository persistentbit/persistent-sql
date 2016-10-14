package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprProperty<T> implements Expr<T> {
    private Expr    parent;
    private String  propertyName;
    private Class<T>  valueClass;
    private String columnName;

    public ExprProperty(Class<T> valueClass,Expr parent, String propertyName, String columnName) {
        this.parent = parent;
        this.propertyName = propertyName;
        this.valueClass = valueClass;
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return parent.toString() + "." + propertyName;
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public Expr getParent() {
        return parent;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<T> getValueClass() {
        return valueClass;
    }

    @Override
    public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(valueClass);
    }
}
