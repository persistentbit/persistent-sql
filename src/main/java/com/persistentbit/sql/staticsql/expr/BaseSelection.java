package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.utils.ToDo;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.QuerySqlBuilder;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.Optional;

/**
 * Created by petermuys on 14/10/16.
 */
public abstract class BaseSelection<T> implements ETypeSelection<T>{

	private final Query query;

	public BaseSelection(Query query, Expr<T> selection) {
		this.query = query;
	}

	@Override
	public ETypeObject<T> withNewParent(ETypePropertyParent newParent) {
		throw new ToDo();
	}

	public Query getQuery() {
		return query;
	}

	public Optional<T> getOneResult() {
		return getResult().headOpt();
	}

	public PList<T> getResult() {
		return query.getDbSql().run(this);
	}

	@Override
	public String _asParentName(ExprToSqlContext context, String propertyName) {
		return context.uniqueInstanceName(this, "Selection") + "." + propertyName;
	}

	@Override
	public PList<Expr<?>> _asExprValues(T value) {
		throw new ToDo();
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		QuerySqlBuilder b = new QuerySqlBuilder(this, context.getDbType());
		return b.generate(context, true);
	}



    /*@Override
	public String _toSql(ExprToSqlContext context) {
        return selections().map(s -> s.expr._toSql(context) + " AS " + s.getPropertyName()).toString(", ");
    }*/

	public class SelectionProperty<E> implements Expr<E>{

		private String  propertyName;
		private Expr<E> expr;

		public SelectionProperty(String propertyName, Expr<E> expr) {
			this.propertyName = propertyName;
			this.expr = expr;
		}

		@Override
		public String toString() {
			return "Selection." + propertyName;
		}


		public ETypeObject getParent() {
			return BaseSelection.this;
		}

		public String getColumnName() {
			return propertyName;
		}

		public String getPropertyName() {
			return propertyName;
		}


		@Override
		public E read(RowReader _rowReader, ExprRowReaderCache _cache) {
			return expr.read(_rowReader, _cache);
		}

		@Override
		public String _toSql(ExprToSqlContext context) {
			return _asParentName(context, propertyName);

		}

		@Override
		public PList<Expr<?>> _expand() {
			return PList.val(expr);
		}

		public Expr<E> _getExpr() {
			return expr;
		}
	}
}
