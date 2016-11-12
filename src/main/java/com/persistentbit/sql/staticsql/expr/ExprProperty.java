package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.Optional;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprProperty<T> implements ETypePropertyParent<T>{

	private ETypePropertyParent parent;
	private String              propertyName;
	private Class<T>            valueClass;
	private String              columnName;

	public ExprProperty(Class<T> valueClass, ETypePropertyParent parent, String propertyName, String columnName) {
		this.parent = parent;
		this.propertyName = propertyName;
		this.valueClass = valueClass;
		this.columnName = columnName;
	}

	@Override
	public String toString() {
		return parent.toString() + "." + propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public String getFullTableName(String schema) {
		return columnName;
	}

	public Class<T> getValueClass() {
		return valueClass;
	}

	@Override
	public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
		return _rowReader.readNext(valueClass);
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return parent._asParentName(context, getColumnName());

	}

	public String getColumnName() {
		return columnName;
	}

	@Override
	public String _fullColumnName(ExprToSqlContext context) {
		String res = parent._fullColumnName(context);
		String colName = getColumnName();
		if(res.isEmpty() == false && colName.isEmpty() == false) {
			res = res + "_";
		}
		return res + colName;
	}

	@Override
	public String _asParentName(ExprToSqlContext context, String propertyName) {
		String colName = getColumnName();
		if(colName.isEmpty() == false){
			colName += "_";
		}
		return getParent().get()._asParentName(context,colName + propertyName);
	}

	public Optional<ETypePropertyParent> getParent() {
		return Optional.of(parent);
	}

	@Override
	public PList<Expr<?>> _expand() {

		Expr subProp = getProperty(parent, propertyName);
		return PList.val(subProp);
	}


	private Expr getProperty(ETypePropertyParent parent, String propertyName) {
		if(parent instanceof ETypeObject) {
			// We have a table column
			ETypeObject                 obj   = (ETypeObject) parent;
			PList<Tuple2<String, Expr>> props = obj._all();
			return props.find(tp -> tp._1.equals(propertyName)).get()._2;
		}
		else if(parent instanceof ExprProperty) {
			//We have a embedded object
			ExprProperty<?>     ep       = (ExprProperty) parent;
			ETypePropertyParent epParent = ep.getParent().get();
			return getProperty(epParent, ep.getPropertyName());
		}
		else {
			throw new RuntimeException("Don't know what to do with:" + parent + " and propertyName " + propertyName);
		}
	}
}
